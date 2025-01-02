package com.buddy.buddy.user;

import com.buddy.buddy.account.DTO.GetUserInformationDTO;
import com.buddy.buddy.account.DTO.UpdateUserInformationDTO;
import com.buddy.buddy.account.controller.UserController;
import com.buddy.buddy.account.entity.Role;
import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.account.repository.UserRepository;
import com.buddy.buddy.account.service.AccountService;
import com.buddy.buddy.account.service.Implementation.AccountServiceImplementation;
import com.buddy.buddy.auth.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ExtendWith(MockitoExtension.class)

@AutoConfigureMockMvc(addFilters = false)
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountServiceImplementation accountServiceImplementation;



    @MockBean
    private JwtUtils jwtUtils;

    private UUID userId;
    private User user;
    private String searchUser = "buddy";

    private GetUserInformationDTO getUserInformationDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("buddy");
        user.setPassword("password");
        user.setEmail("buddy@gmail.com");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setAge(25);
        user.setDescription("buddy");
        user.setAvatar("buddy.png");
        user.setLocked(false);
        userId = user.getId();
        getUserInformationDTO = new GetUserInformationDTO(user);


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    void getUserInformation() throws Exception {
        //Arrange
        User account = new User();
        UUID randomUUID = UUID.randomUUID();
        account.setId(UUID.randomUUID());
        account.setUsername("buddy");
        account.setPassword("password");
        account.setEmail("buddy@gmail.com");
        account.setRole(Role.USER);
        account.setActive(true);

        //Mock
        GetUserInformationDTO getUserInformationDTO = new GetUserInformationDTO(account);
        getUserInformationDTO.setId(randomUUID);
        //Mockito.when(accountServiceImplementation.getAccount(randomUUID)).thenReturn(ResponseEntity.ok(getUserInformationDTO));

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + randomUUID.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(account.getUsername()))
                .andDo(print());
    }

    @Test
    void getBlockedUsers() throws Exception {
        //Arrange
        User account = new User();
        UUID randomUUID = UUID.randomUUID();
        account.setId(UUID.randomUUID());
        account.setUsername("buddy");
        account.setLocked(true);

        //Mock
        GetUserInformationDTO getUserInformationDTO = new GetUserInformationDTO(account);
        getUserInformationDTO.setId(randomUUID);
        //Mockito.when(accountServiceImplementation.getAccount(randomUUID)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User is locked"));

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + randomUUID.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void getDeletedUsers() throws Exception {
        //Arrange
        User account = new User();
        UUID randomUUID = UUID.randomUUID();
        account.setId(UUID.randomUUID());
        account.setUsername("buddy");
        account.setDeleted(true);

        //Mock
        GetUserInformationDTO getUserInformationDTO = new GetUserInformationDTO(account);
        getUserInformationDTO.setId(randomUUID);
        //Mockito.when(accountServiceImplementation.getAccount(randomUUID)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User is locked"));

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + randomUUID.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void searchUser() throws Exception {
        //Arrange
        User account = new User();
        UUID randomUUID = UUID.randomUUID();
        account.setId(randomUUID);
        account.setUsername("buddy");

        //Mock
        GetUserInformationDTO getUserInformationDTO = new GetUserInformationDTO(account);
        Pageable pageable = PageRequest.of(0, 10);
        Page<GetUserInformationDTO> userDTOPage = new PageImpl<>(Collections.singletonList(getUserInformationDTO), pageable, 1);

        when(accountServiceImplementation.searchUser(eq(searchUser), any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(userDTOPage));

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", searchUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("buddy"))
                .andExpect(jsonPath("$.content[0].id").value(randomUUID.toString()))
                .andDo(print());
    }

    @Test
    void searchNotExistUser() throws Exception {
        //Arrange
        Pageable pageable = PageRequest.of(0, 10);

        //Mock
        Page<GetUserInformationDTO> emptyUserDTOPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(accountServiceImplementation.searchUser(eq("noexist"), any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(emptyUserDTOPage));

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "noexist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andDo(print());
    }


    @Test
    void testGetUserListByCriteria_Popularity() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<GetUserInformationDTO> userList = List.of(getUserInformationDTO);
        Page<GetUserInformationDTO> usersPage = new PageImpl<>(userList);

        when(userRepository.findAllByPopularity(pageable)).thenReturn(usersPage);

        // When
        ResponseEntity<Page<GetUserInformationDTO>> response = accountServiceImplementation.getUserListByCriteria("popularity", pageable);

        // Then
        //assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usersPage, response.getBody());
        verify(userRepository, times(1)).findAllByPopularity(pageable);
        verify(userRepository, never()).findAllByCreatedAt(pageable);
    }

    @Test
    void getUserListByCriteriaNewest() throws Exception {
        User newestUser = new User();
        UUID randomUUID = UUID.randomUUID();
        newestUser.setId(randomUUID);
        newestUser.setUsername("buddy");
        newestUser.setPassword("password");
        newestUser.setEmail("buddy@gmail.com");
        newestUser.setCreatedAt(new Date());

        Pageable pageable = PageRequest.of(0, 10);

        GetUserInformationDTO userDTO = new GetUserInformationDTO(newestUser);

        Page<GetUserInformationDTO> userDTOPage = new PageImpl<>(Collections.singletonList(userDTO), pageable, 1);

        when(accountServiceImplementation.getUserListByCriteria(eq("newest"), eq(pageable))).thenReturn(ResponseEntity.ok(userDTOPage));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/list/newest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.content[0].username").value("buddy"))
                //.andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andDo(print());
    }

    @Test
    void testGetUserListByCriteria_InvalidCriteria() throws Exception {
        //Arrange
        Pageable pageable = PageRequest.of(0, 10);

        //Mock
        when(accountServiceImplementation.getUserListByCriteria("__", pageable)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong criteria provided"));

        //Act
        mockMvc.perform(MockMvcRequestBuilders.get("/user/list/__"))
                //.andExpect(status().is5xxServerError())
                .andDo(print());
    }



}
