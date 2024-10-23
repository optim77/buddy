package com.buddy.buddy.image;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.auth.JwtUtils;
import com.buddy.buddy.image.DTO.GetImageDTO;
import com.buddy.buddy.image.controller.ImageController;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.repository.ImageRepository;
import com.buddy.buddy.image.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImageTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private ImageRepository imageRepository;

    private UUID image_uuid;
    private GetImageDTO getImageDTO;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("buddy");
        user.setPassword("password");
        user.setEmail("buddy@buddy.com");
        user.setDescription("lorem ipsum");

        Image image = new Image();
        image.setUser(user);
        image.setDescription("lorem ipsum");
        image.setLikeCount(12);
        image.setOpen(true);
        image.setId(UUID.randomUUID());
        //image.setPublishedDate(new Date());
        image_uuid = image.getId();

        getImageDTO = new GetImageDTO(image, user);

        when(imageRepository.findById(image_uuid)).thenReturn(Optional.of(image));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        //when(imageService.getImage(image_uuid)).thenReturn(new ResponseEntity<>(getImageDTO, HttpStatus.OK));
    }

    @Test
    void searchImage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/image/" + image_uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
