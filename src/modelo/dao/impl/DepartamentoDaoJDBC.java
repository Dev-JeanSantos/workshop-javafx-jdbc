package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import modelo.dao.DepartamentoDao;
import modelo.entidades.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao{

	private Connection conn;
	
	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void inserir(Departamento obj) {
		
		PreparedStatement st = null;		
		try {
			
			st =  conn.prepareStatement("INSERT INTO departamento (nome) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			
			int add = st.executeUpdate();
			
			if (add > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultaSet(rs);
			}
			else {
				throw new DbException("Nenhuma linha foi alterada");
			}
		}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public void update(Departamento obj) {
		
		PreparedStatement st = null;
		try {
			
			st = conn.prepareStatement("UPDATE departamento SET nome = ? WHERE id = ?");
			st.setString(1, obj.getNome());
			st.setInt(2, obj.getId());
			
			st.executeUpdate();
			
		}catch (SQLException e) {
			
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);;
		}		
	}

	@Override
	public void deletarPorId(Integer id) {
		
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM departamento WHERE id = ? ");
			st.setInt(1, id);
			
			int del = st.executeUpdate();
			if(del == 0) {
				
				throw new DbException("Não houve alteração no Banco");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Departamento pesquisarPorId(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			st = conn.prepareStatement("SELECT * FROM departamento WHERE id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				
				Departamento dep = instanciacaoDepartamento(rs);
				return dep;
			}
			return null;			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		
		finally {
			DB.closeStatement(st);
			DB.closeResultaSet(rs);
		}
	}


	@Override
	public List<Departamento> buscarTodos() {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			st = conn.prepareStatement("SELECT * FROM departamento ORDER BY nome;");
			rs = st.executeQuery();
			
			List<Departamento> list = new ArrayList<Departamento>();
			while (rs.next()){
				
				Departamento obj = new Departamento();
				obj.setId(rs.getInt("id"));
				obj.setNome(rs.getString("nome"));
				list.add(obj);				
			}			
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultaSet(rs);			
		}
		
	}
		
	private Departamento instanciacaoDepartamento(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();//instanciando um Objeto Departamento
		dep.setId(rs.getInt("id"));//Setando a Variavel setId com o resultado deparatamentoId
		dep.setNome(rs.getString("nome")); //Setando a Variavel setNome com o resultado deparatamentoNome
		
		return dep;
	}

}
