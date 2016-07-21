/*
 * Copyright [2016] [Jun Ohtani]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.elasticsearch.plugin.ingest.kuromoji_part_of_speech_extract;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.RandomDocumentPicks;
import org.elasticsearch.test.ESTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class KuromojiPartOfSpeechExtractProcessorTests extends ESTestCase {

    public void testThatProcessorWorks() throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put("source_field", "今日は友達と渋谷でランチを食べた。");
        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);
        List<String> posTags = new ArrayList<>();
        // TODO add tests
        posTags.add("名詞-固有名詞-地域-一般");
        posTags.add("名詞-一般");

        KuromojiPartOfSpeechExtractProcessor processor =
            new KuromojiPartOfSpeechExtractProcessor(randomAsciiOfLength(10), "source_field", "target_field", posTags);
        processor.execute(ingestDocument);

        assertThat(ingestDocument.hasField("target_field"), notNullValue());
        assertThat(ingestDocument.getFieldValue("target_field", List.class), equalTo(Arrays.asList("友達", "渋谷", "ランチ")));
    }
}

