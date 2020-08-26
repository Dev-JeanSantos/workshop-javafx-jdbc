package modelo.service;

import java.util.ArrayList;
import java.util.List;

import modelo.entidades.Departamento;

public class DepartamentoService {
	
	public List<Departamento> buscarTodos(){
		
		List<Departamento> list = new ArrayList<Departamento>();
		
		list.add(new Departamento(1,"FULL STACK"));
		list.add(new Departamento(2, "FRONT END"));
		list.add(new Departamento(3, "BACK END"));
		list.add(new Departamento(4, "BANCO DE DADOS"));
		list.add(new Departamento(5, "ANALÍSE DE SISTEMAS"));
		list.add(new Departamento(6, "TI SECURITY"));
		list.add(new Departamento(7, "REDES"));
		list.add(new Departamento(8, "INFRA"));
		list.add(new Departamento(9, "SUPORTE"));
		list.add(new Departamento(10, "ENGENHARIA DE SOFTWARE"));
		
		return list;
	}
}
