create database emprestai_db;

use emprestai_db;

create table tipo_clientes (
    id_tipo_cliente int primary key not null auto_increment,
    dsc_tipo_cliente varchar(45) not null
);

create table clientes (
    id_cliente int primary key not null auto_increment,
    cpf_cliente varchar(11) not null,
    nome_cliente varchar(90) not null,
    score int not null,
    id_tipo_cliente int not null,
    data_nascimento date not null,
    renda_mensal_liquida decimal(10,2) not null,
    renda_familiar_liquida decimal(10,2) not null,
    qtd_pessoas_na_casa int not null,
    foreign key (id_tipo_cliente) references tipo_clientes (id_tipo_cliente)
);

create table servicos (
    id_servico int primary key not null auto_increment,
    dsc_servico varchar(45) not null,
    abrev_servico varchar(3) not null
);

create table status_emprestimos (
    id_status_emprestimo int primary key not null auto_increment,
    dsc_status_emprestimo varchar(45)
);

create table motivo_encerramentos (
    id_motivo_encerramento int primary key not null auto_increment,
    dsc_motivo_encerramento varchar(90)
);

create table emprestimos (
    id_emprestimo int primary key not null auto_increment,
    id_cliente int not null,
    valor_total decimal(10,2) not null,
    quantidade_parcelas int not null,
    juros decimal(5,5) not null,
    data_inicio date not null,
    id_status_emprestimo int not null,
    id_tipo_emprestimo int not null,
    valor_seguro decimal(10,2) not null,
    valor_IOF decimal(10,2) not null,
    outros_custos decimal(10,2) not null,
    data_contratacao date not null,
    id_motivo_encerramento int not null,
    juros_mora decimal(5,5) not null,
    taxa_multa decimal(5,5) not null,
    id_emprestimo_origem int,
    foreign key (id_cliente) references clientes (id_cliente),
    foreign key (id_status_emprestimo) references status_emprestimos (id_status_emprestimo),
    foreign key (id_tipo_emprestimo) references servicos (id_servico),
    foreign key (id_motivo_encerramento) references motivo_encerramentos (id_motivo_encerramento),
    foreign key (id_emprestimo_origem) references emprestimos (id_emprestimo) 
);

create table status_parcelas (
    id_status_parcelas int primary key not null auto_increment,
    dsc_status_parcelas varchar(45) not null
);

create table parcelas (
    id_parcela int primary key not null auto_increment,
    id_emprestimo int not null,
    numero_parcela int not null,
    valor_pago decimal(10,2) not null,
    data_vencimento date not null,
    data_pagamento date not null,
    id_status int not null,
    foreign key (id_emprestimo) references emprestimos (id_emprestimo),
    foreign key (id_status) references status_parcelas (id_status_parcelas)
);