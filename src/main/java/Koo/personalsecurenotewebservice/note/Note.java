package Koo.personalsecurenotewebservice.note;

import Koo.personalsecurenotewebservice.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 해당 클래스에 Auditing 기능을 포함 자세한건 https://webcoding-start.tistory.com/53 참고
public class Note {

    @Id
    @GeneratedValue
    @Column(name = "NOTE_ID")
    private Long id;

    /**
     * 제목
     */
    private String title;

    /**
     * 내용
     */
    @Lob // @Lob은 일반적인 데이터베이스에서 저장하는 길이인 255개 이상의 문자를 저장하고 싶을 때 지정한다. (LOB은 가변길의를 갖는 큰 데이터를 저장하는데 사용하는 데이터형이다. - CLOB은 문자기반을 데이터를 저장하는데 사용된다, BLOB은 binary 데이터를 저장하는데 사용된다.)
    private String content;

    /**
     * User 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @CreatedDate // Auditing 기능 덕분에 Entity가 생성되어 저장될 때 시간이 자동 저장된다.
    private LocalDateTime createdAt;
    @LastModifiedDate // Auditing 기능 덕분에 Entity가 수정 될때 업데이트 시간이 자동 저장된다.
    private LocalDateTime updatedAt;

    public Note(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}