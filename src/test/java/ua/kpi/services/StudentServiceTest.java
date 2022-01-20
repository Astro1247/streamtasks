package ua.kpi.services;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ua.kpi.entities.Exam;
import ua.kpi.entities.Exam.Type;
import ua.kpi.entities.Student;
import ua.kpi.repositories.StudentRepository;

class StudentServiceTest {

  private Student firstStudent = Student.builder()
      .name("1")
      .rating(10)
      .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 181)))
      .build();
  private Student secondStudent = Student.builder()
            .name("2")
            .rating(11)
            .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 182),
                Exam.of(Exam.Type.MATH, 190)))
          .build();
  private Student thirdStudent =
        Student.builder()
            .name("3")
            .rating(11)
            .exams(Arrays.asList(Exam.of(Exam.Type.ENGLISH, 183),
                Exam.of(Exam.Type.MATH, 190)))
      .build();
  private Student fourthStudent = Student.builder()
            .name("4")
            .rating(11)
            .exams(Arrays.asList())
      .build();

  private StudentRepository createStudentRepositoryWithAllStudents(){
    StudentRepository studentRepository = mock(StudentRepository.class);
    List<Student> allStudents = Arrays.asList(firstStudent, secondStudent,
                                thirdStudent, fourthStudent);
    when(studentRepository.findAll()).thenReturn(allStudents);
    return studentRepository;
  }

  @Test
  void should_not_find_student_with_max_math(){
    StudentRepository studentRepository = mock(StudentRepository.class);
    when(studentRepository.findAll()).thenReturn(Arrays.asList(firstStudent,fourthStudent));
    StudentService studentService = new StudentService(studentRepository);
    Optional<Student> studentOpt = studentService.findWithMaxExam(Type.MATH);
    assertEquals(Optional.empty() , studentOpt );
  }

  @Test
  void should_find_students_who_have_enough_math_grade(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    final double mathPassRate = 190.0;
    List<Student> studentsWithMath = studentService.findWithEnoughExam(Type.MATH, mathPassRate);
    assertThat(studentsWithMath, containsInAnyOrder(secondStudent, thirdStudent));
  }

  @Test
  void should_not_find_students_who_have_enough_english_grade(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    final double englishPassRate = 190.0;
    List<Student> studentsWithEnglish = studentService.findWithEnoughExam(Type.ENGLISH, englishPassRate);
    assertThat(studentsWithEnglish, hasSize(0));
  }

  // Task 1
  @Test
  void should_find_student_who_does_not_have_math_grade(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    Optional<Student> studentOpt = studentService.findFirstWithoutSpecifiedExam(Type.MATH);
    assertEquals(Optional.of(firstStudent) , studentOpt );
  }

  // Task 2
  @Test
  void should_find_student_with_math_and_eng_exams_both_enough_grade(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    final double passRate = 180.0;
    List<Student> studentsWithPassGrades = studentService.findWithEnoughExams(passRate);
    assertThat(studentsWithPassGrades, hasSize(2));
  }

  // Task 3
  @Test
  void should_find_student_with_max_english(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    Optional<Student> studentOpt = studentService.findWithMaxExam(Type.ENGLISH);
    assertEquals(Optional.of(thirdStudent) , studentOpt );
  }

  // Task 4
  @Test
  void should_find_students_with_enough_rate_and_english_exam(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    final double ratingPassRate = 11.0;
    List<Student> foundStudents = studentService.findWithPassRatesAndExamType(Type.ENGLISH, ratingPassRate);
    assertThat(foundStudents, hasSize(2));
  }

  // Task 5
  @Test
  void should_find_students_with_enough_exams_count(){
    StudentRepository studentRepository = createStudentRepositoryWithAllStudents();
    StudentService studentService = new StudentService(studentRepository);
    final double requiredExamsCount = 2.0;
    List<Student> foundStudents = studentService.findWithEnoughExamsCount(requiredExamsCount);
    assertThat(foundStudents, hasSize(2));
  }



}