package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabaseClientes;
import com.mycompany.petshop_db.models.clientes;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientesController implements Initializable {

    private String situacao = "";

    @FXML
    private AnchorPane borderPane;
    @FXML
    private Menu activeMenu;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu principal;
    @FXML
    private Menu clientes;
    @FXML
    private Menu pet;
    @FXML
    private Menu produtos;
    @FXML
    private Menu veterinarios;
    @FXML
    private Menu racas;
    @FXML
    private Menu atendentes;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private TableView<clientes> data;
    @FXML
    private TableColumn<clientes, String> cpftable;
    @FXML
    private TableColumn<clientes, String> nometable;
    @FXML
    private TableColumn<clientes, String> emailtable;
    @FXML
    private TableColumn<clientes, String> telefonetable;
    @FXML
    private TableColumn<clientes, String> ceptable;
    @FXML
    private TableColumn<clientes, Integer> numerotable;
    @FXML
    private TableColumn<clientes, String> complementotable;
    @FXML
    private TableColumn<clientes, LocalDate> datatable;
    @FXML
    private TextField numero;
    @FXML
    private TextField telefone;
    @FXML
    private TextField complemento;
    @FXML
    private TextField cpf;
    @FXML
    private TextField nome;
    @FXML
    private TextField email;
    @FXML
    private TextField cep;
    @FXML
    private TextField pesquisar;

    private List<clientes> clientesAdicionados = new ArrayList<>();
    private List<clientes> clientesExcluidos = new ArrayList<>();
    private List<clientes> clientesEditados = new ArrayList<>();
    
    private ChangeListener<String> cpfListener;
    private ChangeListener<String> nomeListener;
    private ChangeListener<String> emailListener;
    private ChangeListener<String> telefoneListener;
    private ChangeListener<String> numeroListener;
    private ChangeListener<String> complementoListener;
    private ChangeListener<String> cepListener;

    @FXML
    private void handleAdicionarOrcamento(ActionEvent event) {
        try {
            loadPage("/fxml/TelaPrincipal.fxml", "Adicionar Orçamento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVisualizarOrcamento(ActionEvent event) {
        try {
            loadPage("/fxml/ExibirOrcamento.fxml", "Visualizar Orçamento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlerClientes(ActionEvent event) throws IOException {
        loadPage("/fxml/Clientes.fxml", "Clientes");
    }

    @FXML
    private void handlerPet() throws IOException {
        loadPage("/fxml/Pet.fxml", "Pet");
    }

    @FXML
    private void handlerProdutos() throws IOException {
        loadPage("/fxml/Produtos.fxml", "Produtos");
    }

    @FXML
    private void handlerVeterinarios() throws IOException {
        loadPage("/fxml/Veterinarios.fxml", "Veterinarios");
    }

    @FXML
    private void handlerRacas() throws IOException {
        loadPage("/fxml/Racas.fxml", "Raças");
    }

    @FXML
    private void handlerAtendentes() throws IOException {
        loadPage("/fxml/Atendentes.fxml", "Atendentes");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Menu defaultMenu = menuBar.getMenus().get(1);
        setActiveMenu(defaultMenu);

        cpftable.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        nometable.setCellValueFactory(new PropertyValueFactory<>("nome"));
        emailtable.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefonetable.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        ceptable.setCellValueFactory(new PropertyValueFactory<>("cep"));
        numerotable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNumero()).asObject());
        complementotable.setCellValueFactory(new PropertyValueFactory<>("complemento"));
        datatable.setCellValueFactory(new PropertyValueFactory<>("dataCadastro"));

        loadClientesData();

        data.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            removeListeners();
            if (newSelection != null) {
                handleSelecionarItem();
                cpf.setDisable(false);
                cpf.setDisable(true);
            }
        });
    }

    private void loadPage(String page, String menuText) throws IOException {
        Stage currentStage = (Stage) borderPane.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle(menuText);
        newStage.show();
    }

    private void loadClientesData() {
        clientesAdicionados.clear();
        clientesExcluidos.clear();
        clientesEditados.clear();

        data.getSelectionModel().clearSelection();

        limparCampos();
        removeListeners();
        List<clientes> clientesList = DatabaseClientes.getUsuariosColuna();
        ObservableList<clientes> observableClientesList = FXCollections.observableArrayList(clientesList);
        data.setItems(observableClientesList);
    }

    private void setFieldsDisabled(boolean disabled) {
        nome.setDisable(disabled);
        email.setDisable(disabled);
        telefone.setDisable(disabled);
        numero.setDisable(disabled);
        complemento.setDisable(disabled);
        cep.setDisable(disabled);
    }

    @FXML
    private void handleAdicionar() {
        removeListeners();

        String cpfText = cpf.getText();
     
        if (Validation.isAnyFieldEmpty(
                cpf.getText(),
                nome.getText(),
                email.getText(),
                telefone.getText(),
                numero.getText(),
                cep.getText()
        )) {
            return;
        }

        if (!Validation.isCPFValido(cpfText)) {
            return;
        }

        if (!Validation.isNumeroInteiro(numero.getText(), "Numero")) {
            return;
        }

        if (!Validation.isTelefoneValido(telefone.getText())) {
            return;
        }

        if (!Validation.isCepValido(cep.getText())) {
            return;
        }

        int num = Integer.parseInt(numero.getText());

        clientes newItem = new clientes(
                cpfText,
                nome.getText(),
                email.getText(),
                telefone.getText(),
                num,
                complemento.getText(),
                cep.getText(),
                LocalDate.now()
        );

        data.getItems().add(newItem);
        clientesAdicionados.add(newItem);
        situacao = "a";
    }

    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();

        ObservableList<clientes> selectedItems = data.getSelectionModel().getSelectedItems();
        if (!clientesAdicionados.isEmpty()) {
            mostrarAlerta("Salve o conteúdo antes de excluir.");
            return;
        }
        situacao = "e";
        
        for (clientes cliente : selectedItems) {
            clientesAdicionados.remove(cliente);
            clientesExcluidos.add(cliente);
        }
        
        data.getItems().removeAll(selectedItems);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        clientes selectedCliente = data.getSelectionModel().getSelectedItem();
        if (!clientesAdicionados.isEmpty()) {
           mostrarAlerta("Salve o conteúdo antes de Edit\\ar");
           return; 
        }
        
        if (selectedCliente != null) {
            cpf.setDisable(true);
            setFieldsDisabled(false);
            situacao = "edt";
            addListeners(selectedCliente);
        }
    }
    @FXML
    private void handleSalvar() {
        removeListeners();        
        switch (situacao) {
            case "a":
                DatabaseClientes.salvarClientes(clientesAdicionados);
                mostrarAlerta("Cliente(s) adicionados com sucesso!");
                break;
            case "e":
                if(!clientesEditados.isEmpty()){
                    for (clientes cliente : clientesEditados) {
                        DatabaseClientes.editarCliente(cliente);
                    }
                }
                for (clientes cliente : clientesExcluidos) {
                    DatabaseClientes.excluirCliente(cliente);
                }
                mostrarAlerta("Cliente(s) excluidos com sucesso!");
                break;
            case "edt":
                if(!clientesExcluidos.isEmpty()){
                    for (clientes cliente : clientesExcluidos) {
                            DatabaseClientes.excluirCliente(cliente);
                        }
                }
                for (clientes cliente : clientesEditados) {
                    if (!Validation.isCPFValido(cliente.getCpf())) {
                        return;
                    }

                    if (!Validation.isNumeroInteiro(String.valueOf(cliente.getNumero()), "Numero")) {
                        return;
                    }

                    if (!Validation.isTelefoneValido(telefone.getText())) {
                        return;
                    }

                    if (!Validation.isCepValido(cep.getText())) {
                        return;
                    }

                    DatabaseClientes.editarCliente(cliente);
                }
                mostrarAlerta("Cliente(s) editados com sucesso!");
                break;
            default:
                break;
        }
        clientesAdicionados.clear();
        clientesExcluidos.clear();
        clientesEditados.clear();
        loadClientesData();
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void setActiveMenu(Menu menu) {
        if (activeMenu != null) {
            activeMenu.setStyle("");
        }
        activeMenu = menu;
        activeMenu.setStyle("-fx-background-color: lightblue;");
    }

    @FXML
    private void handlerPesquisar(ActionEvent event) {
        removeListeners();

        String cpfSearch = cpf.getText();
        String nomeSearch = nome.getText();
        String emailSearch = email.getText();
        String telefoneSearch = telefone.getText();
        String numeroSearch = numero.getText();
        String complementoSearch = complemento.getText();
        String cepSearch = cep.getText();

        List<clientes> resultados = DatabaseClientes.procurarCliente(cpfSearch, nomeSearch, emailSearch, telefoneSearch, numeroSearch, complementoSearch, cepSearch);
        ObservableList<clientes> observableResultados = FXCollections.observableArrayList(resultados);
        data.setItems(observableResultados);
    }

    @FXML
    private void handlerLimpar(ActionEvent event) {
        removeListeners();
        limparCampos();
        loadClientesData();
        cpf.setDisable(false);
        setFieldsDisabled(false);
      
    }
    @FXML
    private void handleCancelar(ActionEvent event){
        clientesAdicionados.clear();
        clientesExcluidos.clear();
        clientesEditados.clear();
        loadClientesData();
    }

    @FXML
    private void handleSelecionarItem() {
        clientes selectedCliente = data.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            cpf.setText(selectedCliente.getCpf());
            nome.setText(selectedCliente.getNome());
            email.setText(selectedCliente.getEmail());
            telefone.setText(selectedCliente.getTelefone());
            numero.setText(Integer.toString(selectedCliente.getNumero()));
            complemento.setText(selectedCliente.getComplemento());
            cep.setText(selectedCliente.getCep());
            setFieldsDisabled(true);
        }
    }

    @FXML
    private void handleAnchorPaneClick(MouseEvent event) {
        removeListeners();
        data.getSelectionModel().clearSelection();
    }

    private void limparCampos() {
        removeListeners();
        cpf.clear();
        nome.clear();
        email.clear();
        telefone.clear();
        numero.clear();
        complemento.clear();
        cep.clear();
    }
    private void addListeners(clientes selectedCliente) {
        cpf.textProperty().addListener(cpfListener = (obs, oldValue, newValue) -> {
            selectedCliente.setCpf(newValue);
            updateTableData();
            updateEditList(selectedCliente);
        });
        nome.textProperty().addListener(nomeListener = (obs, oldValue, newValue) -> {
            selectedCliente.setNome(newValue);
            updateTableData();
            updateEditList(selectedCliente);
        });
        email.textProperty().addListener(emailListener = (obs, oldValue, newValue) -> {
            selectedCliente.setEmail(newValue);
            updateTableData();
            updateEditList(selectedCliente);
        });
        telefone.textProperty().addListener(telefoneListener = (obs, oldValue, newValue) -> {
            selectedCliente.setTelefone(newValue);
            updateTableData();
            updateEditList(selectedCliente);
        });
        numero.textProperty().addListener(numeroListener = (obs, oldValue, newValue) -> {
            if (Validation.isNumeroInteiro(newValue, "Numero")) {
                selectedCliente.setNumero(Integer.parseInt(newValue));
                updateTableData();
                updateEditList(selectedCliente);
            }
        });
        complemento.textProperty().addListener(complementoListener = (obs, oldValue, newValue) -> {
            selectedCliente.setComplemento(newValue);
            updateTableData();
            updateEditList(selectedCliente);
        });
        cep.textProperty().addListener(cepListener = (obs, oldValue, newValue) -> {
            selectedCliente.setCep(newValue);
            updateTableData();
            updateEditList(selectedCliente);
        });
    }
    private void updateEditList(clientes editedCliente) {
        if (!clientesEditados.contains(editedCliente)) {
            clientesEditados.add(editedCliente);
        }
    }
    private void updateTableData() {
        data.refresh(); 
    }


    private void removeListeners() {
        if (cpfListener != null) {
        cpf.textProperty().removeListener(cpfListener);
        }
        if (nomeListener != null) {
            nome.textProperty().removeListener(nomeListener);
        }
        if (emailListener != null) {
            email.textProperty().removeListener(emailListener);
        }
        if (telefoneListener != null) {
            telefone.textProperty().removeListener(telefoneListener);
        }
        if (numeroListener != null) {
            numero.textProperty().removeListener(numeroListener);
        }
        if (complementoListener != null) {
            complemento.textProperty().removeListener(complementoListener);
        }
        if (cepListener != null) {
            cep.textProperty().removeListener(cepListener);
        }
    }
}
