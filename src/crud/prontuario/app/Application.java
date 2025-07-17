package crud.prontuario.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

/**
 * Classe principal do sistema de prontuário médico.
 * Interface de linha de comando para operações CRUD sobre Pacientes e Exames.
 * 
 * Utiliza DAOs para persistência em banco MySQL via JDBC.
 */
public class Application {
    private static final Scanner scanner = new Scanner(System.in); // Scanner para entrada de dados do usuário
    private static final PacienteDAO pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL()); 
    private static final ExameDAO exameDAO = new ExameDAO(new DatabaseConnectionMySQL());
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Formato padrão para datas exibidas

    public static void main(String[] args) {
        int escolha;

        do {
            // Menu principal com opções de operações
            System.out.println("\n==================================================");
            System.out.println("               SISTEMA PRONTUÁRIO MÉDICO           ");
            System.out.println("==================================================");
            System.out.println("1  | Cadastrar Paciente");
            System.out.println("2  | Listar Pacientes");
            System.out.println("3  | Localizar Paciente por ID");
            System.out.println("4  | Editar Paciente");
            System.out.println("5  | Excluir Paciente");
            System.out.println("--------------------------------------------------");
            System.out.println("6  | Cadastrar Exame");
            System.out.println("7  | Listar Exames");
            System.out.println("8  | Localizar Exame por ID");
            System.out.println("9  | Editar Exame");
            System.out.println("10 | Excluir Exame");
            System.out.println("--------------------------------------------------");
            System.out.println("11 | Consultar exames por paciente");
            System.out.println("12 | Exibir pacientes e seus exames");
            System.out.println("0  | Sair");
            System.out.print("\nEscolha uma opção: ");

            escolha = scanner.nextInt();
            scanner.nextLine(); // Consome a quebra de linha para evitar problemas no próximo input

            System.out.println();

            // Switch moderno para direcionar a ação conforme a escolha
            switch (escolha) {
                case 1 -> cadastrarPaciente();
                case 2 -> listarPacientes();
                case 3 -> buscarPaciente();
                case 4 -> atualizarPaciente();
                case 5 -> removerPaciente();
                case 6 -> cadastrarExame();
                case 7 -> listarExames();
                case 8 -> buscarExame();
                case 9 -> atualizarExame();
                case 10 -> removerExame();
                case 11 -> listarExamesDePaciente();
                case 12 -> listarPacientesComExames();
                case 0 -> System.out.println("Encerrando sistema. Até logo!");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }

        } while (escolha != 0);
    }

    /**
     * Cadastro de paciente:
     * Lê nome e CPF do usuário e cria um objeto Paciente para persistir no banco.
     */
    private static void cadastrarPaciente() {
        System.out.println("===== Cadastro de Paciente =====");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        Paciente novoPaciente = new Paciente(null, nome, cpf);
        pacienteDAO.create(novoPaciente);

        System.out.println("\nPaciente cadastrado com sucesso!");
    }

    /**
     * Lista todos os pacientes cadastrados.
     * Formatação da tabela com colunas alinhadas para melhor visualização.
     */
    private static void listarPacientes() {
        List<Paciente> pacientes = pacienteDAO.findAll();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente encontrado.");
            return;
        }

        System.out.println("===== Lista de Pacientes =====");
        System.out.println("+-----+------------------------------+-----------------+");
        System.out.println("| ID  | Nome                         | CPF             |");
        System.out.println("+-----+------------------------------+-----------------+");

        for (Paciente p : pacientes) {
            System.out.printf("| %-3d | %-28s | %-15s |\n", p.getId(), p.getNome(), p.getCpf());
        }

        System.out.println("+-----+------------------------------+-----------------+");
    }

    /**
     * Busca um paciente pelo ID informado.
     * Exibe o resultado com formatação de tabela.
     */
    private static void buscarPaciente() {
        System.out.print("Informe o ID do paciente: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Paciente paciente = pacienteDAO.findById(id);
        if (paciente != null) {
            System.out.println("===== Dados do Paciente =====");
            System.out.println("+-----+------------------------------+-----------------+");
            System.out.printf("| ID  | Nome                         | CPF             |\n");
            System.out.println("+-----+------------------------------+-----------------+");
            System.out.printf("| %-3d | %-28s | %-15s |\n", paciente.getId(), paciente.getNome(), paciente.getCpf());
            System.out.println("+-----+------------------------------+-----------------+");
        } else {
            System.out.println("Paciente não encontrado.");
        }
    }

    /**
     * Atualiza um paciente existente.
     * Permite deixar campos em branco para manter os valores atuais.
     */
    private static void atualizarPaciente() {
        System.out.print("ID do paciente para edição: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Paciente paciente = pacienteDAO.findById(id);
        if (paciente == null) {
            System.out.println("Paciente não encontrado.");
            return;
        }

        System.out.print("Novo nome (deixe em branco para manter atual): ");
        String nome = scanner.nextLine();
        if (!nome.isBlank()) paciente.setNome(nome);

        System.out.print("Novo CPF (deixe em branco para manter atual): ");
        String cpf = scanner.nextLine();
        if (!cpf.isBlank()) paciente.setCpf(cpf);

        pacienteDAO.update(paciente);

        System.out.println("\nPaciente atualizado com sucesso!");
    }

    /**
     * Remove um paciente pelo ID informado.
     * Confirma existência antes de remover.
     */
    private static void removerPaciente() {
        System.out.print("ID do paciente a ser removido: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Paciente paciente = pacienteDAO.findById(id);
        if (paciente != null) {
            pacienteDAO.delete(paciente);
            System.out.println("Paciente removido com sucesso.");
        } else {
            System.out.println("Paciente não encontrado.");
        }
    }

    /**
     * Cadastro de exame:
     * Descrição informada pelo usuário e data do exame é o momento atual.
     * Garante que a data seja sempre a data local do sistema.
     */
    private static void cadastrarExame() {
        System.out.println("===== Cadastro de Exame =====");
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        LocalDateTime dataAtual = LocalDateTime.now(); // Data local do sistema no momento do cadastro

        System.out.print("ID do paciente: ");
        long pacienteId = scanner.nextLong();
        scanner.nextLine();

        Exame exame = new Exame(null, descricao, dataAtual, pacienteId);
        exameDAO.create(exame);

        System.out.println("\nExame cadastrado com sucesso!");
        System.out.println("Data do exame registrada como: " + dataAtual.format(dtf));
    }

    /**
     * Lista todos os exames cadastrados, com formatação tabular.
     */
    private static void listarExames() {
        List<Exame> exames = exameDAO.findAll();
        if (exames.isEmpty()) {
            System.out.println("Nenhum exame encontrado.");
            return;
        }

        System.out.println("===== Lista de Exames =====");
        System.out.println("+-----+------------------------------+---------------------+------------+");
        System.out.println("| ID  | Descrição                    | Data                | PacienteID |");
        System.out.println("+-----+------------------------------+---------------------+------------+");

        for (Exame e : exames) {
            System.out.printf("| %-3d | %-28s | %-19s | %-10d |\n",
                    e.getId(), e.getDescricao(), e.getData().format(dtf), e.getPacienteId());
        }

        System.out.println("+-----+------------------------------+---------------------+------------+");
    }

    /**
     * Busca exame por ID e exibe com formato de tabela.
     */
    private static void buscarExame() {
        System.out.print("ID do exame: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Exame exame = exameDAO.findById(id);
        if (exame != null) {
            System.out.println("===== Dados do Exame =====");
            System.out.println("+-----+------------------------------+---------------------+------------+");
            System.out.printf("| ID  | Descrição                    | Data                | PacienteID |\n");
            System.out.println("+-----+------------------------------+---------------------+------------+");
            System.out.printf("| %-3d | %-28s | %-19s | %-10d |\n",
                    exame.getId(), exame.getDescricao(), exame.getData().format(dtf), exame.getPacienteId());
            System.out.println("+-----+------------------------------+---------------------+------------+");
        } else {
            System.out.println("Exame não encontrado.");
        }
    }

    /**
     * Atualiza exame pelo ID, atualizando descrição se fornecida,
     * e atualizando a data para o momento atual do sistema.
     */
    private static void atualizarExame() {
        System.out.print("ID do exame a ser atualizado: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Exame exame = exameDAO.findById(id);
        if (exame == null) {
            System.out.println("Exame inexistente.");
            return;
        }

        System.out.print("Nova descrição (deixe em branco para manter atual): ");
        String novaDescricao = scanner.nextLine();
        if (!novaDescricao.isBlank()) exame.setDescricao(novaDescricao);

        // Atualiza a data para a hora local atual, garantindo registro correto da alteração
        LocalDateTime dataAtual = LocalDateTime.now();
        exame.setData(dataAtual);

        exameDAO.update(exame);

        System.out.println("\nExame atualizado com sucesso!");
        System.out.println("Data do exame atualizada para: " + dataAtual.format(dtf));
    }

    /**
     * Remove exame pelo ID informado.
     * Confirma existência antes da remoção.
     */
    private static void removerExame() {
        System.out.print("ID do exame para exclusão: ");
        long id = scanner.nextLong();
        scanner.nextLine();

        Exame exame = exameDAO.findById(id);
        if (exame != null) {
            exameDAO.delete(exame);
            System.out.println("Exame removido com sucesso.");
        } else {
            System.out.println("Exame não encontrado.");
        }
    }

    /**
     * Lista exames filtrados pelo ID do paciente informado.
     * Se não houver exames, exibe mensagem amigável.
     */
    private static void listarExamesDePaciente() {
        System.out.print("Informe o ID do paciente: ");
        long idPaciente = scanner.nextLong();
        scanner.nextLine();

        List<Exame> exames = exameDAO.findAll();
        boolean encontrado = false;

        System.out.println("===== Exames do Paciente ID: " + idPaciente + " =====");
        System.out.println("+-----+------------------------------+---------------------+");
        System.out.println("| ID  | Descrição                    | Data                |");
        System.out.println("+-----+------------------------------+---------------------+");

        for (Exame exame : exames) {
            if (exame.getPacienteId() == idPaciente) {
                System.out.printf("| %-3d | %-28s | %-19s |\n",
                        exame.getId(), exame.getDescricao(), exame.getData().format(dtf));
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("|           Nenhum exame encontrado para este paciente            |");
        }

        System.out.println("+-----+------------------------------+---------------------+");
    }

    /**
     * Lista todos pacientes com seus respectivos exames.
     * Se paciente não tiver exames, informa explicitamente.
     */
    private static void listarPacientesComExames() {
        List<Paciente> pacientes = pacienteDAO.findAll();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente encontrado.");
            return;
        }

        System.out.println("===== Pacientes e seus exames =====");

        for (Paciente p : pacientes) {
            System.out.println("\n--------------------------------------------------");
            System.out.printf("Paciente ID: %d | Nome: %s | CPF: %s%n", p.getId(), p.getNome(), p.getCpf());
            System.out.println("--------------------------------------------------");
            System.out.println("+-----+------------------------------+---------------------+");
            System.out.println("| ID  | Descrição                    | Data                |");
            System.out.println("+-----+------------------------------+---------------------+");

            List<Exame> exames = exameDAO.findAll();
            boolean temExames = false;
            for (Exame e : exames) {
                if (e.getPacienteId() == p.getId()) {
                    System.out.printf("| %-3d | %-28s | %-19s |\n",
                            e.getId(), e.getDescricao(), e.getData().format(dtf));
                    temExames = true;
                }
            }
            if (!temExames) {
                System.out.println("|           Nenhum exame associado ao paciente              |");
            }

            System.out.println("+-----+------------------------------+---------------------+");
        }
    }
}
