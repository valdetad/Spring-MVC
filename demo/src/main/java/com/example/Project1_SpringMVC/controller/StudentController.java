package com.example.Project1_SpringMVC.controller;

import com.example.Project1_SpringMVC.data.dtos.StudentCreateDto;
import com.example.Project1_SpringMVC.data.models.Student;
import com.example.Project1_SpringMVC.service.StudentGroupService;
import com.example.Project1_SpringMVC.service.StudentService;
import com.example.Project1_SpringMVC.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentGroupService studentGroupService;

    @Autowired
    private SubjectService subjectService;

    @ResponseBody
    @GetMapping("/rest/all")
    public List<Student> getAllStudentsRest() {
        return studentService.getAllStudents();
    }

    @ResponseBody
    @PostMapping("/rest/add")
    public ResponseEntity<?> addStudentRest(@RequestBody StudentCreateDto studentCreateDto) {
        try {
            Student savedStudent = studentService.saveOrUpdateStudent(studentCreateDto, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping("/rest/edit/{id}")
    public ResponseEntity<?> editStudentRest(@PathVariable("id") int id, @RequestBody StudentCreateDto studentCreateDto) {
        try {
            Student updatedStudent = studentService.saveOrUpdateStudent(studentCreateDto, id);
            if (updatedStudent != null) {
                return ResponseEntity.ok(updatedStudent); // 200 OK
            } else {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ResponseBody
    @DeleteMapping("/rest/delete/{id}")
    public ResponseEntity<Void> deleteStudentRest(@PathVariable("id") int id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping
    public String getAllStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("newStudent", new StudentCreateDto());
        model.addAttribute("studentGroups", studentGroupService.getAllStudentGroups());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "student";
    }

    @PostMapping
    public String saveStudent(@ModelAttribute("student") StudentCreateDto student, Model model) {
        try {
            studentService.saveOrUpdateStudent(student, null);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("studentGroups", studentGroupService.getAllStudentGroups());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            return "student";
        }
        return "redirect:/student";
    }

    @GetMapping("/edit/{id}")
    public String showEditStudentForm(@PathVariable("id") int id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student != null) {
            StudentCreateDto studentDto = new StudentCreateDto();
            studentDto.setId(Long.valueOf(student.getId()));
            studentDto.setFirstName(student.getFirstName());
            studentDto.setLastName(student.getLastName());
            studentDto.setEmail(student.getEmail());
            studentDto.setBirthDate(student.getBirthDate());
            studentDto.setStudentGroupId(student.getStudentGroup().getId());
            model.addAttribute("student", studentDto);
            model.addAttribute("studentGroups", studentGroupService.getAllStudentGroups());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            return "edit-student";
        } else {
            return "redirect:/student";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable("id") int id, @ModelAttribute("student") StudentCreateDto student, Model model) {
        try {
            studentService.saveOrUpdateStudent(student, id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("studentGroups", studentGroupService.getAllStudentGroups());
            model.addAttribute("subjects", subjectService.getAllSubjects());
            return "edit-student";
        }
        return "redirect:/student";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") int id, Model model) {
        studentService.deleteStudent(id);
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("newStudent", new StudentCreateDto());
        model.addAttribute("studentGroups", studentGroupService.getAllStudentGroups());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "redirect:/student";
    }
}