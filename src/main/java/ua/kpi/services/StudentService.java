package ua.kpi.services;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import ua.kpi.entities.Exam;
import ua.kpi.entities.Exam.Type;
import ua.kpi.entities.Student;
import ua.kpi.repositories.StudentRepository;

public class StudentService {

  private StudentRepository studentRepository;

  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }


  public Optional<Student> findWithMaxExam(Type type) {
    List<Student> students = studentRepository.findAll();
    OptionalDouble maxExam = students.stream()
        .flatMap(student -> student.getExams().stream())
        .filter(exam -> exam.getType() == type)
        .mapToDouble(Exam::getScore)
        .max();

    if(!maxExam.isPresent())
      return Optional.empty();

    Exam exam = Exam.of(type,maxExam.getAsDouble());
    return students.stream()
            .filter(student -> student.getExams().contains(exam))
            .findFirst();
  }

  public List<Student> findWithEnoughExam(Type examType, double passRate) {
    return studentRepository.findAll()
        .stream()
        .filter( student -> student.getExams().stream()
                                  .anyMatch(exam -> exam.getType() == examType &&
                                                   exam.getScore() >= passRate ))
        .collect(Collectors.toList());
  }

  public List<Student> findWithEnoughExams(double passRate) {
    return studentRepository.findAll()
            .stream()
            .filter(student -> student.getExams().stream()
                    .anyMatch(exam -> exam.getType() == Type.MATH && exam.getScore()>=passRate) &&
                    student.getExams().stream()
                            .anyMatch(exam -> exam.getType() == Type.ENGLISH && exam.getScore()>=passRate))
            .collect(Collectors.toList());
  }

  public Optional<Student> findFirstWithoutSpecifiedExam(Type examType) {
    return studentRepository.findAll()
            .stream()
            .filter( student -> student.getExams().stream()
                    .allMatch(exam -> exam.getType() != examType ))
            .findFirst();
    /*List<Student> students = studentRepository.findAll();
    for (final Student student : students) {
      for (final Exam exam : student.getExams()) {
        if (exam.getType() == examType) {
          continue;
        }
        return Optional.of(student);
      }
    }
    return Optional.empty();*/
  }

  public List<Student> findWithPassRatesAndExamType(Type examType, double ratingPassRate) {
    return studentRepository.findAll()
            .stream()
            .filter(student -> student.getRating() >= ratingPassRate &&
                    student.getExams()
                            .stream()
                            .anyMatch(exam -> exam.getType() == examType))
            .collect(Collectors.toList());
  }

  public List<Student> findWithEnoughExamsCount(double requiredExamsCount) {
    return studentRepository.findAll()
            .stream()
            .filter(student -> student.getExams().size() == requiredExamsCount)
            .collect(Collectors.toList());
  }
}
