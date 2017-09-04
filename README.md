[![Build Status](https://travis-ci.org/dhiegohenrique/cloud-api.svg?branch=master)](https://travis-ci.org/dhiegohenrique/cloud-api)

API Rest que provê instâncias de cloud para as pessoas cadastradas. Estão disponíveis operações de CRUD e a autenticação ocorre por JSON Web Tokens.

Requisitos:
1) Java 8 (http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html);
2) Maven 3 ou superior (https://maven.apache.org/download.cgi);
3) MongoDB 3.4 ou superior e seu serviço deve estar rodando (https://www.mongodb.com/download-center#community);
	
Para rodar:
1) Importar como projeto Maven, clicar com o direito em CloudApiApplication e "Run as Java Application";
2) A aplicação estará rodando em: http://localhost:3000

Para rodar os testes:
1) mvn test;

A cada commit, serão realizados testes unitários no Travis. Se passarem, o deploy será realizado em https://eb-cloud-api.herokuapp.com/