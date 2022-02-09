package bek.dev.studentcrud.repository;

import bek.dev.studentcrud.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
