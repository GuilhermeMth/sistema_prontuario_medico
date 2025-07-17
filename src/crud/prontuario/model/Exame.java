package crud.prontuario.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Exame {

	private Long id;
	private String descricao;
	private LocalDateTime data;
	private Long pacienteId;
	
	public Exame() {
		super();
	}

	public Exame(Long id, String descricao) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.data = LocalDateTime.now();
	}
	
	public Exame(Long id, String descricao, Long pacienteId) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.data = LocalDateTime.now();
		this.pacienteId = pacienteId;
	}
	
	public Exame(Long id, String descricao, LocalDateTime data, Long pacienteId) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.data = data;
		this.pacienteId = pacienteId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
	public Long getPacienteId() {
		return pacienteId;
	}
	
	public void setPacienteId(Long pacienteId) {
		this.pacienteId = pacienteId;
	}

	@Override
	public String toString() {
		return "Exame [id=" + id + ", descricao=" + descricao + ", data=" + data + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Exame other = (Exame) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
	
}
