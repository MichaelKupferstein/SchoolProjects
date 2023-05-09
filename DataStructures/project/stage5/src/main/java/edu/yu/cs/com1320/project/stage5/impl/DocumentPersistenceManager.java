package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;



import static jakarta.xml.bind.DatatypeConverter.printBase64Binary;
import static jakarta.xml.bind.DatatypeConverter.printByte;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private GsonBuilder gsonBuilder;
    private Gson gson;
    private File baseDir;

    public DocumentPersistenceManager(File baseDir){
        this.baseDir = baseDir;
        this.gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentSerializer());
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer());
        this.gson = gsonBuilder.create();

    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        String authority = uri.getAuthority();//get the begining of the uri
        String uriPath = uri.getRawPath();//get the uri as a path
        //System.out.println("URI Path: "+uriPath);
        String fileName = getFileNameFromUri(uri);
        String absoluteFileName = fileName.replace(".json","");

        Path path = Path.of(this.baseDir + "\\" + authority + uriPath.replace(absoluteFileName, ""));//create a path using all the stuff
        //System.out.println("Path: " + path);
        Files.createDirectories(path);//create the acutal file path

        File jsonFile = new File(path.toFile(),fileName);//create the new file
        try {
            FileWriter fileWriter = new FileWriter(jsonFile);
            fileWriter.write(this.gson.toJson(val,DocumentImpl.class).toString());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public Document deserialize(URI uri) throws IOException {//if its byte need to decode!!
        String authority = uri.getAuthority();//get the begining of the uri
        String uriPath = uri.getRawPath();//get the uri as a path
        String fileName = getFileNameFromUri(uri);

        //File file = new File(uriPath.toString(),fileName);
        Path path = Path.of(this.baseDir + "\\" + authority + uriPath + ".json");
        System.out.println("inDes Path:" + path);
        String file = new String(Files.readAllBytes(path));
        System.out.println("inDes File: " + file + "\n");
        JsonObject jsonObject = this.gson.fromJson(file, JsonObject.class);

//        Document temp = new DocumentImpl(uri,jsonObject.get("DocumentContent").toString());
        Document temp = this.gson.fromJson(jsonObject,DocumentImpl.class);

        return temp;
    }

    private String getFileNameFromUri(URI uri){
        String[] paths = uri.getPath().split("/");//get the name of the file
        String fileName = paths[paths.length - 1] + ".json";
        return fileName;
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

//                String gsonMapString = gson.toJson(document.getWordMap());
                String jsonMap = gson.toJson(document.getWordMap());
                jsonObject.add("WordMap", new JsonPrimitive(jsonMap));
            }

            System.out.println("inDocSer JSON file: " + jsonObject + "\n");
            return jsonObject;
        }
    }

    private JsonObject createMapAsJsonObject(Map<String,Integer> map){
        JsonObject results = new JsonObject();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            results.addProperty(entry.getKey(), entry.getValue());
        }
        return results;
    }


    private class DocumentDeserializer implements JsonDeserializer<Document>{
        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Document results;
            String uriAsString = jsonObject.get("uri").toString().replaceAll("\"", "");
            URI uri;
            JsonElement wordMap = jsonObject.get("WordMap");
            try {
                uri = new URI(uriAsString);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            if(wordMap != null){//i.e its text doc
                results = new DocumentImpl(uri, String.valueOf(jsonObject.get("DocumentContent")));
            }else {//i.e its binary... NEEDS DECODING
                results = new DocumentImpl(uri, String.valueOf(jsonObject.get("DocumentContent")));
            }
            System.out.println("inDocDes got here\n");
            return results;
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        DocumentPersistenceManager dp = new DocumentPersistenceManager(new File(System.getProperty("user.dir")));
        URI uri = new URI("https://www.yu.edu/documents/doc1");
//        System.out.println("inMain " + uri);
        Document doc1 = new DocumentImpl(uri,"This is a test document to test this class");
        dp.serialize(uri,doc1);

        URI uri2 = new URI("https://www.yu.edu/documents/doc2");
//        System.out.println("inMain " + uri2);
        String btyes = "This is a test for binary documents";
        Document doc2 = new DocumentImpl(uri2,btyes.getBytes());
        dp.serialize(uri2,doc2);

        Document test = dp.deserialize(uri);
        System.out.println("inMain " + test + "\n");
        Document test2 = dp.deserialize(uri2);
        System.out.println("inMain " + test2 + "\n");
    }
}
