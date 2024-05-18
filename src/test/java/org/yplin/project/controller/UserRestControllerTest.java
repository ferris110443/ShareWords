package org.yplin.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserDto;
import org.yplin.project.data.form.SignupForm;
import org.yplin.project.service.UserService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserRestController.class)
public class UserRestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @InjectMocks // create instance of class under test and inject mock
    private UserRestController userRestController;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build(); // instantiate MockMVC instance simulate HTTP request
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this); //initialize fields annotated with Mockito annotations like @Mock, @InjectMocks
    }

    @Test
    public void testSignUpSuccess() throws Exception {

        UserDto userDto = new UserDto(1L, "username", "email@example.com", "https://testUrl");
        SignupForm signupForm = new SignupForm("username", "email@example.com", "password");
        SignInDto signInDto = new SignInDto("access_token123", 3600L, userDto);
        when(userService.signup(any(SignupForm.class))).thenReturn(signInDto);

        String signupFormJsonString =
                objectMapper.writeValueAsString(signupForm);
        String expectedJson = """
                {
                    "data": {
                        "access_token": "access_token123",
                        "access_expired": 3600,
                        "user": {
                            "id": 1,
                            "name": "username",
                            "email": "email@example.com",
                            "user_image_url": "https://testUrl"
                        }
                    }
                }
                """;


        // When & Then
        mockMvc.perform(post("/api/1.0/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupFormJsonString))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));


//                .andExpect(jsonPath("$.data.access_token").value("access_token123"))
//                .andExpect(jsonPath("$.data.access_expired").value(3600))
//                .andExpect(jsonPath("$.data.user.id").value(1L))
//                .andExpect(jsonPath("$.data.user.name").value("username"))
//                .andExpect(jsonPath("$.data.user.email").value("email@example.com"))
//                .andExpect(jsonPath("$.data.user.user_image_url").value("https://testUrl"));
    }

}
