package com.tarefa03.ti2;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

/**
 * Classe Principal (Entry Point) da Aplicação Web.
 * Orquestra as rotas HTTP e inicializa o servidor web embutido (Jetty) do Spark.
 */
public class Aplicacao {

    public static void main(String[] args) {
        
        // 1. CONFIGURAÇÃO DO SERVIDOR WEB
        // Define a porta TCP onde o servidor vai "escutar". O padrão da web de testes é 8080 ou 6789.
        port(6789);
        
        // Diz ao Spark: "Se alguém pedir um arquivo que você não conhece, procure na pasta /public"
        // É assim que o nosso index.html e o CSS são servidos para o navegador.
        staticFiles.location("/public");
        
        // Instancia o nosso DAO para ter acesso aos métodos de banco de dados
        ProdutoDAO dao = new ProdutoDAO();

        // =========================================================================
        // ROTA POST: CADASTRAR PRODUTO (Recebe os dados do formulário HTML)
        // =========================================================================
        post("/produto/inserir", (request, response) -> {
            
            // O objeto 'request' contém tudo o que o navegador enviou no corpo da requisição.
            // O método queryParams() busca os valores baseados no atributo 'name' das tags <input> do HTML.
            String nome = request.queryParams("nome");
            double preco = Double.parseDouble(request.queryParams("preco"));
            int quantidade = Integer.parseInt(request.queryParams("quantidade"));
            
            // Instancia o modelo em memória com os dados capturados da rede
            Produto produto = new Produto(0, nome, preco, quantidade);
            
            // Tenta persistir no disco rígido via PostgreSQL
            if (dao.inserir(produto)) {
                // Se o banco confirmou a gravação, instruímos o navegador do usuário a mudar de página
                response.redirect("/produto/listar");
                return ""; // O Spark exige que toda rota retorne algo, mesmo após um redirect
            } else {
                response.status(500);
                return "<h1>Erro Crítico: Falha ao gravar no Banco de Dados.</h1> <a href='/'>Voltar</a>";
            }
        });

        get("/produto/listar", (request, response) -> {
            
            List<Produto> produtos = dao.listar();
            
            StringBuilder html = new StringBuilder();
            
            html.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>");
            html.append("<title>Produtos Cadastrados</title>");
            html.append("<style>body{font-family:Arial; padding:40px;} table{width:100%; border-collapse:collapse;} th,td{border:1px solid #ddd; padding:8px; text-align:left;} th{background-color:#f2f2f2;}</style>");
            html.append("</head><body>");
            
            html.append("<h2>Lista de Produtos em Estoque</h2>");
            html.append("<table>");
            html.append("<tr><th>ID</th><th>Nome</th><th>Preço (R$)</th><th>Quantidade</th></tr>");
            
            for (Produto p : produtos) {
                html.append("<tr>");
                html.append("<td>").append(p.getId()).append("</td>");
                html.append("<td>").append(p.getNome()).append("</td>");
                html.append("<td>").append(String.format("%.2f", p.getPreco())).append("</td>");
                html.append("<td>").append(p.getQuantidade()).append("</td>");
                html.append("</tr>");
            }
            
            html.append("</table>");
            html.append("<br><br>");
            html.append("<a href='/' style='padding:10px; background:#0056b3; color:white; text-decoration:none; border-radius:4px;'>+ Cadastrar Novo</a>");
            
            html.append("</body></html>");
            
            response.type("text/html");
            
            return html.toString();
        });

        System.out.println("=================================================");
        System.out.println("  Servidor Spark INICIADO com sucesso!");
        System.out.println("  Ouvindo na porta: 6789");
        System.out.println("  Acesse no seu navegador: http://localhost:6789/");
        System.out.println("=================================================");
    }
}