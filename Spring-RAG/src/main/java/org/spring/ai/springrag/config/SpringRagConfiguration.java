package org.spring.ai.springrag.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class SpringRagConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRagConfiguration.class);

    @Bean
    public Function<Flux<byte[]>, Flux<Document>> documentLoader() {
        return resourceFlux -> resourceFlux.map(fileBytes -> new TikaDocumentReader(new ByteArrayResource(fileBytes))
                .read().getFirst()).subscribeOn(Schedulers.boundedElastic());
    }

    @Bean
    public Function<Flux<Document>, Flux<List<Document>>> splitter() {
//        var splitter = TokenTextSplitter.builder()
//                .withChunkSize(500)
//                .withKeepSeparator(false)
//                .withMinChunkLengthToEmbed(10)
//                .build();
        var splitter = TokenTextSplitter.builder().build();
        return documentFlux -> documentFlux.map(incoming -> splitter.
                        apply(List.of(incoming)))
                .subscribeOn(Schedulers.boundedElastic());
    }


    @Bean
    public Consumer<Flux<List<Document>>> vectorStoreConsumer(VectorStore vectorStore) {
        return documentFlux -> documentFlux
                .doOnNext(documents -> {
                    if (!documents.isEmpty()) {
                        var docCount = documents.size();
                        LOGGER.info("Writing {} documents to vector store.", docCount);

                        vectorStore.accept(documents);

                        LOGGER.info(
                                "{} documents have been written to vector store.", docCount);
                    }
                })
                .subscribe();
    }

    @Bean
    ApplicationRunner go(FunctionCatalog catalog) {
        Runnable composedFunction = catalog.lookup(null);
        return args -> {
            composedFunction.run();
        };
    }

}
