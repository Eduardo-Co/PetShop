package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabaseProdutos;
import com.mycompany.petshop_db.models.produtos;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ProdutosController implements Initializable {
    
    private Menu activeMenu;
    private String situacao = "";
    
    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<produtos> data;
    @FXML
    private TableColumn<produtos, Integer> idtable;
    @FXML
    private TableColumn<produtos, String> nometable;
    @FXML
    private TableColumn<produtos, Double> precotable;
    @FXML
    private TableColumn<produtos, String> tipotable;
    @FXML
    private TableColumn<produtos, String> crmvtable;
    @FXML
    private TableColumn<produtos, String> descricaotable;
    @FXML
    private ComboBox<String> tipo;
    @FXML
    private Button adicionar;
    @FXML
    private Button excluir;
    @FXML
    private Button salvar;
    @FXML
    private TextField nome;
    @FXML
    private TextField preco;
    @FXML
    private TextField pesquisar;
    @FXML
    private Button btnpesquisar;
    @FXML
    private Button btnlimpar;
    @FXML
    private TextField descricao;
    @FXML
    private TextField crmv;

     
    private List<produtos> produtosAdicionados = new ArrayList<>();
    private List<produtos> produtosExcluidos = new ArrayList<>();
    private List<produtos> produtosEditados = new ArrayList<>();
    

    private ChangeListener<String> nomeListener;
    private ChangeListener<String> precoListener;
    private ChangeListener<String> tipoListener;
    private ChangeListener<String> crmvListener;
    private ChangeListener<String> descricaoListener;
    
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
    private void handlePet(ActionEvent event) throws IOException {
        loadPage("/fxml/Pet.fxml", "Pet");
    }

    @FXML
    private void handleProdutos(ActionEvent event) throws IOException {
        loadPage("/fxml/Produtos.fxml", "Produtos");
    }

    @FXML
    private void handleVeterinarios(ActionEvent event) throws IOException {
        loadPage("/fxml/Veterinarios.fxml", "Veterinarios");
    }

    @FXML
    private void handleRacas(ActionEvent event) throws IOException {
        loadPage("/fxml/Racas.fxml", "Raças");
    }

    @FXML
    private void handleAtendentes(ActionEvent event) throws IOException {
        loadPage("/fxml/Atendentes.fxml", "Atendentes");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Menu defaultMenu = menuBar.getMenus().get(3);
        setActiveMenu(defaultMenu);
        
        ObservableList<String> tipoOptions = FXCollections.observableArrayList("PRODUTO", "SERVIÇO");
        tipo.setItems(tipoOptions);
        
        idtable.setCellValueFactory(new PropertyValueFactory<>("id"));
        nometable.setCellValueFactory(new PropertyValueFactory<>("nome"));
        precotable.setCellValueFactory(new PropertyValueFactory<>("preco"));
        tipotable.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        crmvtable.setCellValueFactory(new PropertyValueFactory<>("crmv"));
        descricaotable.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        
        loadProdutosData();
        
        data.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            removeListeners();
            if (newSelection != null) {
                handleSelecionarItem();
            }
        });
    }
    
    private void loadPage(String page, String menuText) throws IOException {
        Stage currentStage = (Stage) data.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle(menuText);
        newStage.show();
    }
    
    private void loadProdutosData() {
        produtosAdicionados.clear();
        produtosExcluidos.clear();
        produtosEditados.clear();
        data.getSelectionModel().clearSelection();
        limparCampos();
        removeListeners();
        
        List<produtos> produtosList = DatabaseProdutos.getProdutos();
        ObservableList<produtos> observableProdutosList = FXCollections.observableArrayList(produtosList);
        data.setItems(observableProdutosList);
        
        
    }
 
    @FXML
    private void handleAdicionar(ActionEvent event) {

        if (Validation.isAnyFieldEmpty(
                nome.getText(),
                preco.getText(),
                tipo.getValue(),
                crmv.getText()
            )) {
            return;
        }

        double precoNum;
        
        try {
            precoNum = Double.parseDouble(preco.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Preço deve ser um número válido.");
            return;
        }

        int novoId = determinarNovoId();
        produtos newProduto = new produtos(nome.getText(), descricao.getText(), precoNum, tipo.getValue(),crmv.getText());
        newProduto.setId(novoId);

        data.getItems().add(newProduto);
        produtosAdicionados.add(newProduto);
        situacao = "a";
    }

    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();
        ObservableList<produtos> selectedItems = data.getSelectionModel().getSelectedItems();
        
        if (!produtosAdicionados.isEmpty()) {
            mostrarAlerta("Salve o conteúdo antes de excluir.");
            return;
        }

        situacao = "e";
        
        for (produtos produto : selectedItems) {
            produtosAdicionados.remove(produto);
            produtosExcluidos.add(produto);
        }
        data.getItems().removeAll(selectedItems);
    }
    @FXML
    private void handleEditar(ActionEvent event) {
        produtos selectedPet = data.getSelectionModel().getSelectedItem();
        if (!produtosAdicionados.isEmpty()) {
           mostrarAlerta("Salve o conteúdo antes de Editar");
           return; 
        }
       
        if (selectedPet != null) {
            setFieldsDisabled(false);
            situacao = "edt";
            addListeners(selectedPet);
        }
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        removeListeners();

        switch (situacao) {
            case "a":
                DatabaseProdutos.salvarProduto(produtosAdicionados);
                mostrarAlerta("Produto(s) adicionados com sucesso!");
                break;
            case "e":
                if (!produtosEditados.isEmpty()) {
                    for (produtos produto : produtosEditados) {
                        DatabaseProdutos.editarProduto(produto);
                    }
                }
                for (produtos produto : produtosExcluidos) {
                    DatabaseProdutos.excluirProduto(produto);
                }
                mostrarAlerta("Produto(s) excluidos com sucesso!");
                break;
            case "edt":
                if (!produtosExcluidos.isEmpty()) {
                    for (produtos produto : produtosExcluidos) {
                        DatabaseProdutos.excluirProduto(produto);
                    }
                }
                for (produtos produto : produtosEditados) {
                    double preco = produto.getPreco();
                    if (preco <= 0) {
                        mostrarAlerta("O preço deve ser um número positivo.");
                        return;
                    }
                    DatabaseProdutos.editarProduto(produto);
                }
                mostrarAlerta("Produto(s) editados com sucesso!");
                break;
            default:
                break;
        }
        produtosAdicionados.clear();
        produtosExcluidos.clear();
        produtosEditados.clear();
        loadProdutosData();
    }     
    @FXML
    private void handlePesquisar(ActionEvent event) {
        String nomeSearch = nome.getText();
        String precoSearch = preco.getText();
        String tipoSearch = tipo.getValue();
        String crmvSearch = crmv.getText();
        String descricaoSearch = descricao.getText();

        List<produtos> resultados = DatabaseProdutos.procurarProduto(nomeSearch, precoSearch, tipoSearch, crmvSearch, descricaoSearch);
        ObservableList<produtos> observableResultados = FXCollections.observableArrayList(resultados);
        data.setItems(observableResultados);
    }

    @FXML
    private void handleLimpar(ActionEvent event){
        loadProdutosData();
        limparCampos();
        setFieldsDisabled(false);
        loadProdutosData();
    }  
    @FXML
    private void handleCancelar(ActionEvent event) {
        produtosAdicionados.clear();
        produtosExcluidos.clear();
        produtosEditados.clear();
        loadProdutosData();
    }
    @FXML
    private void handleSelecionarItem() {
        produtos selectedProduto = data.getSelectionModel().getSelectedItem();
        if (selectedProduto != null) {
            nome.setText(selectedProduto.getNome());
            preco.setText(String.valueOf(selectedProduto.getPreco()));
            tipo.setValue(selectedProduto.getTipo());
            crmv.setText(selectedProduto.getCrmv());
            descricao.setText(selectedProduto.getDescricao());
            setFieldsDisabled(true);

        }
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

    private int determinarNovoId() {
        if (data.getItems().isEmpty()) {
            return 1;
        } else {
            produtos ultimoProduto = data.getItems().get(data.getItems().size() - 1);
            return ultimoProduto.getId() + 1;
        }
    }

    @FXML
    private void handleAnchorPaneClick(MouseEvent event) {
        removeListeners();
        data.getSelectionModel().clearSelection();
        setFieldsDisabled(false);
    }

    private void limparCampos() {
        removeListeners();
        nome.clear();
        preco.clear();
        crmv.clear();
        descricao.clear();
    }

    private void addListeners(produtos selectedProduto) {
        nome.textProperty().addListener(nomeListener = (obs, oldValue, newValue) -> {
            selectedProduto.setNome(newValue);
            updateTableData();
            updateEditList(selectedProduto);
        });
        preco.textProperty().addListener(precoListener = (obs, oldValue, newValue) -> {
            try {
                selectedProduto.setPreco(Double.parseDouble(newValue));
                updateTableData();
                updateEditList(selectedProduto);
            } catch (NumberFormatException e) {
                mostrarAlerta("Preço deve ser um número válido.");
            }
        });
        tipo.valueProperty().addListener(tipoListener = (obs, oldValue, newValue) -> {
            selectedProduto.setTipo(newValue);
            updateTableData();
            updateEditList(selectedProduto);
        });
        crmv.textProperty().addListener(crmvListener = (obs, oldValue, newValue) -> {
            selectedProduto.setCrmv(newValue);
            updateTableData();
            updateEditList(selectedProduto);
        });
        descricao.textProperty().addListener(descricaoListener = (obs, oldValue, newValue) -> {
            selectedProduto.setDescricao(newValue);
            updateTableData();
            updateEditList(selectedProduto);
        });
    }

    private void updateEditList(produtos editedProduto) {
        if (!produtosEditados.contains(editedProduto)) {
            produtosEditados.add(editedProduto);
        }
    }

    private void updateTableData() {
        data.refresh(); 
    }

    private void removeListeners() {
        if (nomeListener != null) {
            nome.textProperty().removeListener(nomeListener);
        }
        if (precoListener != null) {
            preco.textProperty().removeListener(precoListener);
        }
        if (tipoListener != null) {
            tipo.valueProperty().removeListener(tipoListener);
        }
        if (crmvListener != null) {
            crmv.textProperty().removeListener(crmvListener);
        }
        if (descricaoListener != null) {
            descricao.textProperty().removeListener(descricaoListener);
        }
    }
    private void setFieldsDisabled(boolean disabled) {
        nome.setDisable(disabled);
        preco.setDisable(disabled);
        tipo.setDisable(disabled);
        crmv.setDisable(disabled);
        descricao.setDisable(disabled);
    }
}
