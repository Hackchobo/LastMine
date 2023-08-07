package com.green.jpaexam.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Table(name = "t_product")
@EqualsAndHashCode // 동등성=데이터가 같으면, 동일성=주소값이 같으면
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity { // 가장중요
    // 모든 테이블에는 PK가 있어야 한다. 이유는 데이터를 구분짓기 위해서

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long number;

    @Column(nullable = false) // notNull 임
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @CreationTimestamp
    private LocalDateTime createdAt;    // 사용자가 게시판 같은것을 올릴때는 거진 필수로 올린다.

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
