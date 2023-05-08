package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;



import static jakarta.xml.bind.DatatypeConverter.printBase64Binary;
import static jakarta.xml.bind.DatatypeConverter.printByte;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private GsonBuilder gsonBuilder;
    private Gson gson;

    public DocumentPersistenceManager(File baseDir){
        this.gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Document.class, new DocumentSerializer());
        gsonBuilder.registerTypeAdapter(Document.class, new DocumentDeserializer());
        this.gson = gsonBuilder.create();

    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        String j = this.gson.toJson(val);
        System.out.println("Serialized doc: " + j);
    }


    @Override
    public Document deserialize(URI uri) throws IOException {
//        Document j = this.gson.fromJson(uri);
//        return j;
        return null;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        return false;
    }

    private class DocumentSerializer implements JsonSerializer<Document>{
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("uri", new JsonPrimitive(document.getKey().toString()));
            if(document.getDocumentTxt() == null){//i.e its binary doc
                String base64Encoded = printBase64Binary(document.getDocumentBinaryData());
                jsonObject.add("DocumentContent", new JsonPrimitive(base64Encoded));
            }else if(document.getDocumentBinaryData() == null) {//i.e its a text doc
                jsonObject.add("DocumentContent",new JsonPrimitive(document.getDocumentTxt()));
            }
            jsonObject.add("WordMap", new JsonPrimitive(document.getWordMap().toString()));
            return jsonObject;
        }
    }

    private class DocumentDeserializer implements JsonDeserializer<Document>{
        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Document results;
            String uriAsString = jsonObject.get("uri").toString();
            URI uri;
            Map<String, Integer> wordMap = (Map<String, Integer>) jsonObject.get("WordMap");
            try {
                uri = new URI(uriAsString);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            results = new DocumentImpl(uri, String.valueOf(jsonObject.get("DocumentContent")));
            results.setWordMap(wordMap);
            return results;
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        DocumentPersistenceManager dp = new DocumentPersistenceManager(new File(System.getProperty("user.dir")));
        URI uri = new URI("https://www.yu.edu/documents/doc1");
        Document doc1 = new DocumentImpl(uri,"This is a test document to test this class");
        dp.serialize(uri,doc1);

        URI uri2 = new URI("https://www.yu.edu/documents/doc2");
        String btyes = "This is a test for binary documents";
        Document doc2 = new DocumentImpl(uri2,btyes.getBytes());
        dp.serialize(uri2,doc2);

    }
}
