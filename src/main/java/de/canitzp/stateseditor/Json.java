package de.canitzp.stateseditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Json {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(UUID.class, new TypeAdapter<UUID>() {
        @Override
        public void write(JsonWriter out, UUID value) throws IOException {
            TypeAdapters.UUID.write(out, value);
        }

        @Override
        public UUID read(JsonReader in) throws IOException {
            String s = GSON.fromJson(in, String.class);
            if(s != null && !s.isEmpty() && !"null".equals(s)){
                return UUID.fromString(s);
            }
            return null;
        }
    }).setPrettyPrinting().create();

    private static final Map<Object, File> loadedFiles = new ConcurrentHashMap<>();

    public static <T> T read(File file, Class<T> type){
        try {
            T instance = GSON.fromJson(new FileReader(file), type);
            loadedFiles.put(instance, file);
            return instance;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void write(Object obj){
        try {
            File file = loadedFiles.get(obj);
            if(file != null){
                GSON.toJson(obj, new FileWriter(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Chunk {
        public int x;
        public int z;
        public long price;
        public int district;
        public long created;
        public UUID creator;
        public long changed;
        public Type type;
        public UUID owner;
        public long last_tax_collection;
        public long last_save;

        public static enum Type {
            NORMAL,
            PUBLIC,
            PRIVATE,
            COMPANY,
            DISTRICT,
            MUNICIPAL,
            STATEOWNED
        }
    }

    public static class District {
        public int id;
        public Type type;
        public long created;
        public UUID creator;
        public long changed;
        public String name;
        public int municipality;
        public List<Integer> neighbors;
        public UUID manager;
        public String color;
        public boolean can_foreigners_settle;
        public long price;
        public String icon;
        public int chunks;
        public boolean unclaim_chunks_if_bankrupt;
        public long last_save;

        public static enum Type {
            WILDERNESS,
            AGRICULTURAL,
            MINERAL,
            VILLAGE,
            RESIDENTAL,
            COMMERCIAL,
            INDUSTRIAL,
            WASTELAND,
            MUNICIPAL
        }
    }

    public static class Municipality {
        public int id;
        public String name;
        public long created;
        public UUID creator;
        public long changed;
        public UUID mayor;
        public List<Integer> neighbors;
        public List<Integer> districs;
        public List<UUID> citizen;
        public List<UUID> council;
        public int state;
        public long balance;
        public String color;
        public boolean open;
        public long price;
        public String icon;
        public boolean kick_if_bankrupt;
        public long citizen_tax;
        public long last_save;
    }

    public static class State {
        public int id;
        public String name;
        public long created;
        public UUID creator;
        public long changed;
        public UUID leader;
        public List<Integer> neighbors;
        public List<Integer> municipalities;
        public int capital;
        public List<UUID> council;
        public long balance;
        public String color;
        public List<Integer> blacklist;
        public long price;
        public String icon;
        public long last_save;
    }

}
