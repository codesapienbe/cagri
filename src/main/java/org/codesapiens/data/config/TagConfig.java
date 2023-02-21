package org.codesapiens.data.config;

import org.codesapiens.data.entity.TagEntity;
import org.codesapiens.data.service.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.stream.Stream;

@Configuration
public class TagConfig implements CommandLineRunner {

    private final TagRepository tagRepository;

    public TagConfig(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Tags with @ annotation

        TagEntity annotation01 = new TagEntity("afad", '@');

        Stream.of(
                annotation01
        ).forEach(ann -> {
            Optional<TagEntity> foundTag = tagRepository.findByTitle(ann.getTitle());
            if (foundTag.isEmpty()) {
                tagRepository.save(ann);
            }
        });

        // Tags with # hashtag

        TagEntity hashtag01 = new TagEntity("depremzede", '#');
        TagEntity hashtag02 = new TagEntity("deprem", '#');
        TagEntity hashtag03 = new TagEntity("earthquake", '#');
        TagEntity hashtag04 = new TagEntity("uzaktan", '#');

        Stream.of(
                hashtag01,
                hashtag02,
                hashtag03,
                hashtag04
        ).forEach(item -> {
            Optional<TagEntity> foundTag = tagRepository.findByTitle(item.getTitle());
            if (foundTag.isEmpty()) {
                tagRepository.save(item);
            }
        });

    }
}
