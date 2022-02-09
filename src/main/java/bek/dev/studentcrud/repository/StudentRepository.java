package bek.dev.studentcrud.repository;

import bek.dev.studentcrud.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {



//    @Query(value = "select * from employee  where LOWER(first_name) LIKE %:firstName% and LOWER(last_name) LIKE %:lastName% and LOWER(email) like %:email%", nativeQuery = true)
//    Page<Employee> findAllLike( String firstName,  String lastName,  String email , Pageable pageable);
    }
