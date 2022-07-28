package Koo.personalsecurenotewebservice.note;

import Koo.personalsecurenotewebservice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    /**
     * 노트(게시글) 조회
     *
     * @return 노트 view (note/index.html)
     */
    @GetMapping
    public String getNote(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal(); // 로그인한 사용자 정보를 가져온다.
        List<Note> notes = noteService.findByUser(user);
        // note/index.html 에서 notes 사용가능
        model.addAttribute("notes", notes);
        // note/index.html 제공
        return "note/index";
    }

    /**
     * 노트 저장
     */
    @PostMapping
    public String saveNote(Authentication authentication, @ModelAttribute NoteRegisterDto noteDto) {
        User user = (User) authentication.getPrincipal();
        noteService.saveNote(user, noteDto.getTitle(), noteDto.getContent());
        return "redirect:/note";
    }

    /**
     * 노트 삭제
     */
    @DeleteMapping
    public String deleteNote(Authentication authentication, @RequestParam Long id) {
        User user = (User) authentication.getPrincipal();
        noteService.deleteNote(user, id);
        return "redirect:/note";
    }
}

