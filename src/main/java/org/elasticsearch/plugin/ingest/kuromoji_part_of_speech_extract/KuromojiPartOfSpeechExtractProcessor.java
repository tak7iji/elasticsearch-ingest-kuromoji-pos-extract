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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizerFactory;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.analysis.JapanesePartOfSpeechKeepFilterFactory;
import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.ingest.ConfigurationUtils.readList;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class KuromojiPartOfSpeechExtractProcessor extends AbstractProcessor {

    public static final String TYPE = "kuromoji_pos_extract";

    private final String field;
    private final String targetField;
    private final List<String> posTags;

    private final Analyzer kuromojiAnalyzer;

    public KuromojiPartOfSpeechExtractProcessor(String tag, String field, String targetField, List<String> posTags) throws IOException {
        super(tag);
        this.field = field;
        this.targetField = targetField;
        this.posTags = posTags;
        this.kuromojiAnalyzer = loadAnalyzer(posTags);
    }

    // TODO should refactor to allow user to specify settings more
    private Analyzer loadAnalyzer(List<String> posTags) {
        Map<String, String> tokenizerOptions = new HashMap<>();
        tokenizerOptions.put("mode", JapaneseTokenizer.Mode.NORMAL.toString());
        TokenizerFactory tokenizerFactory = new JapaneseTokenizerFactory(tokenizerOptions);
        TokenFilterFactory[] tokenFilterFactories = new TokenFilterFactory[1];
        tokenFilterFactories[0] = new JapanesePartOfSpeechKeepFilterFactory(new HashMap<>(), this.posTags);

        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                Tokenizer tokenizer = tokenizerFactory.create();
                TokenStream tokenStream = tokenizer;
                for (TokenFilterFactory tokenFilterFactory : tokenFilterFactories) {
                    tokenStream = tokenFilterFactory.create(tokenStream);
                }
                return new TokenStreamComponents(tokenizer, tokenStream);
            }
        };

        return analyzer;
    }

    @Override
    public void execute(IngestDocument ingestDocument) throws Exception {
        String content = ingestDocument.getFieldValue(field, String.class);
        // tokenizer with part-of-speech
        List<String> filteredTokens = new ArrayList<>();
        if (Strings.isNullOrEmpty(content) == false) {
            try (TokenStream tokens = this.kuromojiAnalyzer.tokenStream("field", content)) {
                tokens.reset();
                CharTermAttribute termAttr = tokens.getAttribute(CharTermAttribute.class);
                while (tokens.incrementToken()) {
                    filteredTokens.add(termAttr.toString());
                }
                tokens.end();

                ingestDocument.setFieldValue(targetField, filteredTokens);
            }
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {

        @Override
        public KuromojiPartOfSpeechExtractProcessor create(Map<String, Processor.Factory> factories, String processorTag, Map<String, Object> config) throws Exception {
            String field = readStringProperty(TYPE, processorTag, config, "field");
            String targetField = readStringProperty(TYPE, processorTag, config, "target_field", "default_field_name");

            List<String> posTags = readList(TYPE, processorTag, config, "pos_tags");

            return new KuromojiPartOfSpeechExtractProcessor(processorTag, field, targetField, posTags);
        }
    }
}
