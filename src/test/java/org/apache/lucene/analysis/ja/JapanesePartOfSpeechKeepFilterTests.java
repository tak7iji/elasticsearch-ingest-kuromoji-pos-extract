/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.analysis.ja;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.IOUtils;
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JapanesePartOfSpeechKeepFilterTests extends BaseTokenStreamTestCase {
    private Analyzer analyzer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Set<String> keepTags = new HashSet<>();
        keepTags.add("名詞-固有名詞-地域-一般");
        keepTags.add("名詞-一般");

        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer source = new JapaneseTokenizer(newAttributeFactory(), null, true, JapaneseTokenizer.Mode.SEARCH);
                TokenStream stream = new JapanesePartOfSpeechKeepFilter(source, keepTags);
                return new TokenStreamComponents(source, stream);
            }
        };
    }

    @Override
    public void tearDown() throws Exception {
        IOUtils.close(analyzer);
        super.tearDown();
    }

    public void testKeepPartOfSpeech() throws IOException {
        assertAnalyzesTo(analyzer, "今日は友達と渋谷でランチを食べた。",
            new String[]{"友達", "渋谷", "ランチ"});
    }

}
