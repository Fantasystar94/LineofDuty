package com.example.lineofduty.user;
import com.example.lineofduty.common.model.enums.Role;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.domain.user.repository.UserBatchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserBatchRepositoryTest {

    @Autowired
    UserBatchRepository userBatchRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static final int TOTAL_USERS = 100_000;
    private static final int BATCH_SIZE = 5_000;

    @Test
    void bulkInsert_users_real_db() {

        System.out.println("유저 벌크 생성 시작");

        List<User> batch = new ArrayList<>(BATCH_SIZE);
        String encodedPassword = passwordEncoder.encode("1234!");

        for (int i = 0; i < TOTAL_USERS; i++) {

            User user = new User(
                    "bulkUser" + i,
                    "bulkUser" + i + "@realdb.com",
                    encodedPassword,
                    Role.ROLE_USER,
                    "900101-1" + String.format("%06d", i)
            );

            batch.add(user);

            if (batch.size() == BATCH_SIZE) {
                userBatchRepository.batchInsert(batch);
                batch.clear();
                System.out.println("[ Inserted ] " + (i + 1));
            }
        }

        if (!batch.isEmpty()) {
            userBatchRepository.batchInsert(batch);
        }

        System.out.println("유저 10만명 DB INSERT 완료");
    }
}

