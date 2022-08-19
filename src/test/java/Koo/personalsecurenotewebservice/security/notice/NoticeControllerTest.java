package Koo.personalsecurenotewebservice.security.notice;

import Koo.personalsecurenotewebservice.notice.Notice;
import Koo.personalsecurenotewebservice.notice.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class NoticeControllerTest {

    @Autowired
    private NoticeRepository noticeRepository;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity()) // MockMvc에 스프링시큐리티 적용
                .alwaysDo(print())
                .build();
    }

    @Test
    void getNotice_인증없음() throws Exception {
        mockMvc.perform(get("/notice"))
                .andExpect(redirectedUrlPattern("**/login"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser // 가짜 유저를 스프링 시큐리티에 등록 후 테스트 수행
    void getNotice_인증있음() throws Exception {
        mockMvc.perform(get("/notice"))
                .andExpect(status().isOk())
                .andExpect(view().name("notice/index"));
    }

    @Test
    void postNotice_인증없음() throws Exception {
        mockMvc.perform(
                post("/notice")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
        ).andExpect(status().isForbidden()); // 접근 거부
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "admin", password = "admin")
    void postNotice_유저인증있음() throws Exception {
        mockMvc.perform(
                post("/notice").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
        ).andExpect(status().isForbidden()); // 접근 거부
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin", password = "admin")
    void postNotice_어드민인증있음() throws Exception {
        mockMvc.perform(
                post("/notice").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
        ).andExpect(redirectedUrl("notice")).andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteNotice_인증없음() throws Exception {
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        mockMvc.perform(delete("/notice?id=" + notice.getId()))
                .andExpect(status().isForbidden()); // 접근 거부
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "admin", password = "admin")
    void deleteNotice_유저인증있음() throws Exception {
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        mockMvc.perform(delete("/notice?id=" + notice.getId()).with(csrf()))
                .andExpect(status().isForbidden()); // 접근 거부
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin", password = "admin")
    void deleteNotice_어드민인증있음() throws Exception {
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        mockMvc.perform(delete("/notice?id=" + notice.getId()).with(csrf()))
                .andExpect(redirectedUrl("notice")).andExpect(status().is3xxRedirection());
    }
}