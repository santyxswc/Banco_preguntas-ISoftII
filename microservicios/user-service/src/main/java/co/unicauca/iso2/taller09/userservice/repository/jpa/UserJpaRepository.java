package co.unicauca.iso2.taller09.userservice.repository.jpa;

import co.unicauca.iso2.taller09.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    List<UserEntity> findAllByOrderByIdAsc();

    @Query("select distinct u from UserEntity u join u.roles r where r = :role order by u.id")
    List<UserEntity> findByRole(@Param("role") Role role);
}
