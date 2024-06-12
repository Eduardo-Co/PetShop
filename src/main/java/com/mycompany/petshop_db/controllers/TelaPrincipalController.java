package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabaseInitializer;
import com.mycompany.petshop_db.dao.DatabaseOrcamento;
import com.mycompany.petshop_db.models.OrcamentoTabela;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TelaPrincipalController {

    @FXML
    private AnchorPane mainContent;

    @FXML
    private MenuBar menuBar;

    private Menu activeMenu;

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
    private TableView<OrcamentoTabela> data;

    @FXML
    private TableColumn<OrcamentoTabela, Integer> idtable;

    @FXML
    private TableColumn<OrcamentoTabela, String> cpftable;

    @FXML
    private TableColumn<OrcamentoTabela, Integer> quantidadetable;

    @FXML
    private TableColumn<OrcamentoTabela, BigDecimal> precotable;

    @FXML
    private TableColumn<OrcamentoTabela, Integer> produtoidtable;

    @FXML
    private TableColumn<OrcamentoTabela, String> formapagamentotable;

    @FXML
    private TableColumn<OrcamentoTabela, String> datatable;

    @FXML
    private TableColumn<OrcamentoTabela, Integer> petidtable;

    @FXML
    private ComboBox<String> formapagamento;

    @FXML
    private TextField quantidade;

    @FXML
    private TextField produtoid;

    @FXML
    private TextField preco;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button adicionar;

    @FXML
    private Button excluir;

    @FXML
    private Button salvar;

    @FXML
    private TextField cpf;

    @FXML
    private TextField petid;
    
    private AnchorPane currentPageContent;

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
    private void handleClientes(ActionEvent event) throws IOException {
        loadPage("/fxml/Clientes.fxml", "Clientes");
    }

    @FXML
    private void handlePet() throws IOException {
        loadPage("/fxml/Pet.fxml", "Pet");
    }

    @FXML
    private void handleProdutos() throws IOException {
        loadPage("/fxml/Produtos.fxml", "Produtos");
    }

    @FXML
    private void handleVeterinarios() throws IOException {
        loadPage("/fxml/Veterinarios.fxml", "Veterinarios");
    }

    @FXML
    private void handleRacas() throws IOException {
        loadPage("/fxml/Racas.fxml", "Raças");
    }

    @FXML
    private void handleAtendentes() throws IOException {
        loadPage("/fxml/Atendentes.fxml", "Atendentes");
    }
    
    @FXML
    private void initialize() {
        Menu defaultMenu = menuBar.getMenus().get(0);
        setActiveMenu(defaultMenu);
        loadFormasPagamento();

        idtable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        cpftable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        quantidadetable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantidade()).asObject());
        precotable.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPreco()));
        produtoidtable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProdutoId()).asObject());
        formapagamentotable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormaPagamento()));
        datatable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData()));
        petidtable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPetId()).asObject());
    }

    @FXML
    private void handleAdicionar() {
        String cpfText = cpf.getText(); 
        String quantidadeText = quantidade.getText();
        String precoText = preco.getText();
        String produtoIdText = produtoid.getText();
        String formaPagamentoText = formapagamento.getValue();
        LocalDate dataValue = datePicker.getValue();
        String dataText = dataValue != null ? dataValue.toString() : ""; // Verificar se a dataValue é nula
        String petIdText = petid.getText();

        if (Validation.isAnyFieldEmpty(cpfText, quantidadeText, precoText, produtoIdText, formaPagamentoText, dataText, petIdText)) {
            return;
        }
        if (!Validation.isCPFValido(cpfText)) {
            return;
        }
       
        if(!Validation.isNumeroInteiro(petIdText,"Pet")) return;
        if(!Validation.isNumeroInteiro(produtoIdText,"Produto")) return;
        if(!Validation.isNumeroInteiro(quantidadeText,"Quantidade")) return;

        int produtoId = Integer.parseInt(produtoIdText);
        int petId = Integer.parseInt(petIdText);
        int quantidade = Integer.parseInt(quantidadeText);

        OrcamentoTabela newItem = new OrcamentoTabela(
            data.getItems().size() + 1, 
            cpfText, 
            quantidade,
            new BigDecimal(precoText),
            produtoId,
            formaPagamentoText,
            dataText,
            petId
        );

        data.getItems().add(newItem);
    }
    
    private void loadPage(String page, String menuText) throws IOException {
        Stage currentStage = (Stage) mainContent.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle(menuText);
        newStage.show();
    }

   
    private void setActiveMenu(Menu menu) {
        if (activeMenu != null) {
            activeMenu.setStyle(""); 
        }
        activeMenu = menu;
        activeMenu.setStyle("-fx-background-color: lightblue;"); 
    }

    private void loadFormasPagamento() {
        List<String> formasPagamentoList = DatabaseInitializer.getFormasPagamento();
        System.out.println("Lista de formas de pagamento recebida: " + formasPagamentoList);

        ObservableList<String> observableFormasPagamentoList = FXCollections.observableArrayList(formasPagamentoList);
        formapagamento.setItems(observableFormasPagamentoList);
        formapagamento.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleExcluir(ActionEvent event) {
        ObservableList<OrcamentoTabela> selectedItems = data.getSelectionModel().getSelectedItems();
        data.getItems().removeAll(selectedItems);
    }  

    @FXML
    private void handleSalvar(ActionEvent event) {
        Map<String, Integer> cpfParaIdCabecalho = new HashMap<>();
        ObservableList<OrcamentoTabela> orcamentoItens = data.getItems();
        for (OrcamentoTabela item : orcamentoItens) {
            String cpfCliente = item.getCpf();
            String formaPagamento = item.getFormaPagamento();
            LocalDate selectedDate = datePicker.getValue();
            int idPet = item.getPetId();

            if (cpfParaIdCabecalho.containsKey(cpfCliente)) {
                int idCabecalho = cpfParaIdCabecalho.get(cpfCliente);
                DatabaseOrcamento.salvarOrcamentoItem(item, idCabecalho);
            } else {
                int idCabecalho = DatabaseOrcamento.salvarOrcamentoCabecalho(cpfCliente, formaPagamento, selectedDate, idPet);
                cpfParaIdCabecalho.put(cpfCliente, idCabecalho);
                DatabaseOrcamento.salvarOrcamentoItem(item, idCabecalho);
            }
        }
    }
}
