package in.anujjamdade.groceryapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        // Create a client using the provided URI (may or may not include a database)
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    private String ensureUriHasDatabase(String uri, String database) {
        // If the URI already contains a path portion (database), replace it with the configured database.
        // Otherwise append the database before query params.
        // Examples handled:
        //  - mongodb://host:27017 -> mongodb://host:27017/database
        //  - mongodb://host:27017/oldDb -> mongodb://host:27017/database
        //  - mongodb://host:27017/?authSource=admin -> mongodb://host:27017/database?authSource=admin

        if (uri == null || uri.isEmpty()) {
            return uri;
        }
        String prefix;
        String rest;
        int idx = uri.indexOf("?");
        if (idx >= 0) {
            prefix = uri.substring(0, idx);
            rest = uri.substring(idx); // includes '?'
        } else {
            prefix = uri;
            rest = "";
        }

        // Now remove any existing database path from prefix
        // Find the first occurrence of "/" after the scheme (mongodb:// or mongodb+srv://)
        int schemeSep = prefix.indexOf("://");
        int slashAfterHost = -1;
        if (schemeSep >= 0) {
            slashAfterHost = prefix.indexOf('/', schemeSep + 3);
        } else {
            slashAfterHost = prefix.indexOf('/');
        }

        String withoutDb;
        if (slashAfterHost >= 0) {
            withoutDb = prefix.substring(0, slashAfterHost); // strip existing '/...'
        } else {
            withoutDb = prefix;
        }

        return withoutDb + "/" + database + rest;
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDbFactory() {
        // Use a connection string that explicitly contains the configured database name.
        String uriWithDb = ensureUriHasDatabase(mongoUri, mongoDatabase);
        log.info("Using MongoDB connection string: {}", uriWithDb);
        return new SimpleMongoClientDatabaseFactory(uriWithDb);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        return new MongoTemplate(factory);
    }
}
