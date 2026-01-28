//package com.example.lineofduty;
//import com.example.lineofduty.common.exception.CustomException;
//import com.example.lineofduty.common.exception.ErrorMessage;
//import com.example.lineofduty.domain.enlistmentSchedule.ApplicationStatus;
//import com.example.lineofduty.common.model.enums.Role;
//import com.example.lineofduty.domain.enlistmentSchedule.repository.EnlistmentScheduleRepository;
//import com.example.lineofduty.domain.product.repository.ProductRepository;
//import com.example.lineofduty.domain.user.repository.UserRepository;
//import com.example.lineofduty.domain.enlistmentSchedule.EnlistmentSchedule;
//import com.example.lineofduty.domain.product.Product;
//import com.example.lineofduty.domain.user.User;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.temporal.TemporalAdjusters;
//
//@Component
//@RequiredArgsConstructor
//public class InitData {
//    private final UserRepository userRepository;
//    private final EnlistmentScheduleRepository enlistmentScheduleRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final ProductRepository productRepository;
//
//    @PostConstruct
//    @Transactional
//    public void init() {
//        // 중복 생성 방지
//        if (userRepository.count() > 0) {
//            return;
//        }
//        // 이미 데이터 있으면 스킵
//        if (enlistmentScheduleRepository.count() > 0) {
//            return;
//        }
//
//        // 이미 상품 있으면 스킵
//        if (productRepository.count() > 0) {
//            return;
//        }
//
//        User admin = new User(
//                "관리자",
//                "admin@example.com",
//                passwordEncoder.encode("1234!"),
//                Role.ROLE_ADMIN,
//                "941229-1182611"
//        );
//
//        User user = new User(
//                "유저",
//                "user@example.com",
//                passwordEncoder.encode("1234!"),
//                Role.ROLE_ADMIN,
//                "941229-1182611"
//        );
//
//        userRepository.save(admin);
//        userRepository.save(user);
//
//
//        //2026년 동안 화요일마다 4회씩 생성
//        int year = 2026;
//
//        for (int month = 1; month <= 12; month++) {
//
//            LocalDate firstTuesday = LocalDate.of(year, month, 1)
//                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
//
//            for (int i = 0; i < 4; i++) {
//                LocalDate enlistmentDate = firstTuesday.plusWeeks(i);
//
//                EnlistmentSchedule schedule = new EnlistmentSchedule();
//                schedule.setEnlistmentDate(enlistmentDate);
//                schedule.setCapacity(100);
//                schedule.setRemainingSlots(100);
//
//                enlistmentScheduleRepository.save(schedule);
//            }
//        }
//
//
//        productRepository.save(
//                new Product(
//                        "왕고무링",
//                        "짬의 상징 왕 고무링, 훈련소에서부터 착용해보세요",
//                        10000,
//                        100,
//                        ApplicationStatus.ProductStatus.ON_SALE
//                )
//        );
//
//        productRepository.save(
//                new Product(
//                        "물집 안생기는 깔창",
//                        "물집은 자랑이 아닙니다. 미리 예방하세요",
//                        6000,
//                        100,
//                        ApplicationStatus.ProductStatus.ON_SALE
//                )
//        );
//
//    }
//}
