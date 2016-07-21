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

import org.elasticsearch.node.NodeModule;
import org.elasticsearch.plugins.Plugin;

import java.io.IOException;

public class IngestKuromojiPartOfSpeechExtractPlugin extends Plugin {

    public void onModule(NodeModule nodeModule) throws IOException {
        nodeModule.registerProcessor(KuromojiPartOfSpeechExtractProcessor.TYPE,
            (registry) -> new KuromojiPartOfSpeechExtractProcessor.Factory());
    }

}
