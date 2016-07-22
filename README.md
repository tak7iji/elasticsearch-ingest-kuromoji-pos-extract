# Elasticsearch Kuromoji_part_of_speech_extract Ingest Processor

This processor is extracting terms what you specify part-of-speech tags in `pos_tags`
and stores the output in the JSON before it is being stored.
This plugin uses codelibs's lucene-analyzers-kuromoji-ipadic-neologd and
[LUCENE-7273](https://issues.apache.org/jira/browse/LUCENE-7273) patch.

* JapaneseTokenizer's mode is NORMAL

## Usage


```
PUT _ingest/pipeline/kuromoji-pos-pipeline
{
  "description": "A pipeline to extract terms using kuromoji part-of-speech filter",
  "processors": [
    {
      "kuromoji_pos_extract" : {
        "field" : "my_field",
        "target_field" : "noun_only_field",
        "pos_tags" : ["名詞-固有名詞-地域-一般", "名詞-一般"]
      }
    }
  ]
}

PUT /my-index/my-type/1?pipeline_id=opennlp-pipeline
{
  "my_field" : "美味しいお寿司を大手町で食べました。"
}

GET /my-index/my-type/1
{
  "my_field" : "美味しいお寿司を大手町で食べました。"
  "noun_only_field": []
}
```

## Configuration

| Parameter | Use |
| --- | --- |
| field   | Field name of where to read the content from |
| target_field  | Field name to extract terms|
| pos_tags  | Part-of-speech tags what you want to extract |

## Setup

In order to install this plugin, you need to create a zip distribution first by running

```bash
gradle clean check
```

This will produce a zip file in `build/distributions`.

After building the zip file, you can install it like this

```bash
bin/plugin install file:///path/to/elasticsearch-ingest-kuromoji-pos-extract/build/distribution/ingest-kuromoji_part_of_speech_extract-0.0.1-SNAPSHOT.zip
```

## Bugs & TODO

* We cannnot set any options, token_filter, char_filter. It is helpful if we can set.


