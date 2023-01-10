package com.webapp.tdastore;

import com.webapp.tdastore.upload.CloudinaryUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TdaStoreApplicationTests {

    @Test
    void contextLoads() {
        String id = CloudinaryUtils.getPublicId("https://res.cloudinary.com/dddb8btv0/image/upload/v1673351470/fjzp6oxncho5b7zj06fv.png");

    }

}
