package com.example.Project1_SpringMVC.controller;
import com.example.Project1_SpringMVC.data.dtos.StudentGroupCreateDto;
import com.example.Project1_SpringMVC.data.models.StudentGroup;
import com.example.Project1_SpringMVC.service.StudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/student-group")
public class StudentGroupController {

    @Autowired
    private StudentGroupService studentGroupService;

    @ResponseBody
    @GetMapping("/rest/all")
    public List<StudentGroup> getAllStudentGroups(){
        return studentGroupService.getAllStudentGroups();
    }

    @GetMapping
    public String getAllStudentGroups(Model model) {
        List<StudentGroup> studentGroups = studentGroupService.getAllStudentGroups();
        model.addAttribute("studentGroups", studentGroups);
        model.addAttribute("newStudentGroup", new StudentGroupCreateDto());
        return "groups"; // returns groups.html Thymeleaf template
    }


    @GetMapping("/add")
    public String showAddStudentGroupForm(Model model) {
        model.addAttribute("studentGroup", new StudentGroupCreateDto());
        return "add-group";
    }


    @PostMapping("/add")
    public String addStudentGroup(@ModelAttribute("studentGroup") StudentGroupCreateDto studentGroupDto) {
        StudentGroup studentGroup = mapToStudentGroup(studentGroupDto);
        studentGroupService.saveOrUpdateStudentGroup(studentGroup);
        return "redirect:/student-group";
    }

    // REST Add
    @ResponseBody
    @PostMapping("/rest/add")
    public ResponseEntity<StudentGroup> addStudentGroupRest(@RequestBody StudentGroupCreateDto studentGroupDto) {
        StudentGroup studentGroup = mapToStudentGroup(studentGroupDto);
        StudentGroup savedStudentGroup = studentGroupService.saveOrUpdateStudentGroup(studentGroup);
        return ResponseEntity.status(201).body(savedStudentGroup); // 201 Created
    }

    @GetMapping("/edit/{id}")
    public String showEditStudentGroupForm(@PathVariable("id") int id, Model model) {
        StudentGroup existingStudentGroup = studentGroupService.getStudentGroupById(id);
        if (existingStudentGroup != null) {
            model.addAttribute("studentGroup", existingStudentGroup);
            return "edit-group";
        } else {
            return "redirect:/student-group";
        }
    }

    // REST Edit
    @ResponseBody
    @PostMapping("/rest/edit/{id}")
    public ResponseEntity<StudentGroup> editStudentGroupRest(@PathVariable("id") int id, @RequestBody StudentGroupCreateDto studentGroupCreateDto) {
        StudentGroup studentGroup = mapToStudentGroup(studentGroupCreateDto);
        studentGroup.setId(id);
        StudentGroup updatedStudentGroup = studentGroupService.saveOrUpdateStudentGroup(studentGroup);
        if (updatedStudentGroup != null) {
            return ResponseEntity.ok(updatedStudentGroup); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @PostMapping("/edit/{id}")
    public String editStudentGroup(@PathVariable("id") int id, @ModelAttribute("studentGroup") StudentGroupCreateDto studentGroupDto) {
        StudentGroup studentGroup = mapToStudentGroup(studentGroupDto);
        studentGroup.setId(id);
        studentGroupService.saveOrUpdateStudentGroup(studentGroup);
        return "redirect:/student-group";
    }

    // REST Delete
    @DeleteMapping("/rest/delete/{id}")
    public ResponseEntity<Void> deleteStudentGroupRest(@PathVariable("id") int id) {
        try {
            studentGroupService.deleteStudentGroup(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (DataIntegrityViolationException e) {
            // Handle specific exception for constraint violation
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteStudentGroup(@PathVariable("id") int id) {
        studentGroupService.deleteStudentGroup(id);
        return "redirect:/student-group";
    }


    private StudentGroup mapToStudentGroup(StudentGroupCreateDto studentGroupDto) {
        StudentGroup studentGroup = new StudentGroup();
        studentGroup.setName(studentGroupDto.getName());
        return studentGroup;
    }
}
