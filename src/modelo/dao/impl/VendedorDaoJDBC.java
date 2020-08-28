package modelo.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import modelo.dao.VendedorDao;
import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;

public class VendedorDaoJDBC implements VendedorDao {
	//Instanciando o classe Conexao
	private Connection conn;
	//Instanciando DaoJDBC que entrega o Objeto Conexao para acessar em todas as classes
	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void inserir(Vendedor obj) {
		
		PreparedStatement st = null;
		try {
			
			st = conn.prepareStatement(
					"INSERT INTO vendedor "
					+ "( nome, email, dataNascimento, salario, departamentoid )"
					+ "VALUES "
					+ " (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3,new java.sql.Date (obj.getDataNascimento().getTime()));
			st.setDouble(4, obj.getSalario());
			st.setInt(5,obj.getDepartamento().getId());
			
			int add = st.executeUpdate();
			
			if (add > 0) {
				
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				
				DB.closeResultaSet(rs);
				
			}
			else {
				
				throw new DbException("Nenhuma Linha foi Alterada");
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);			
		}
	}

	@Override
	public void update(Vendedor obj) {
		
		PreparedStatement st = null;
			
		try {
			
			st = conn.prepareStatement(
					"UPDATE  vendedor "
					+ " SET nome = ?, email = ?, dataNascimento = ?, salario = ?, departamentoid = ? "
					+ "WHERE id = ? ");
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3,new java.sql.Date (obj.getDataNascimento().getTime()));
			st.setDouble(4, obj.getSalario());
			st.setInt(5,obj.getDepartamento().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
						
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);			
	}
}
		

	@Override
	public void deletarPorId(Integer id) {
		
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM vendedor WHERE id = ? ");
			st.setInt(1, id);
			
			int del = st.executeUpdate();
				
			if (del == 0) {
				
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
	public Vendedor pesquisarPorId(Integer id) { //Buscar objeto Vendedor + departamento por VendedorId
		
		PreparedStatement st = null; //Iniciando estrutura sql com null
		ResultSet rs = null;//Inciando retorno dos dados do bd com null
		
		try {
			st = conn.prepareStatement( //sql que busca vendedor e departamento po vendedorId
					"SELECT vendedor .* "
					+ ", departamento.nome "
					+ "AS depNome"
					+" FROM vendedor "
					+ " INNER JOIN  departamento "
					+ "ON vendedor.departamentoid = departamento.id "
					+ "WHERE vendedor.id = ? ");
			
			st.setInt(1,id);//Entrada do vendedorId a pesquisar na consulta atraves "?"
			rs = st.executeQuery();//execução da query retornando para resultset
			if (rs.next()) { //condição de busca ate encontrar o ultimo dado no banco
												
				Departamento dep = instanciacaoDepartamento(rs);//Metodo para chamada de departamento instanciado
							
				Vendedor obj = instanciacaoVendedor(rs, dep);
				
				return obj;//retorna o objeto completo
			}
			return null; //se na pesquisar não encontra nada retorna null
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		
		finally {
			
			DB.closeStatement(st);
			DB.closeResultaSet(rs);
			//não há necessidade de fechar a conexão pois outro metodos podem utiliza-lá
		}
	}
	
	@Override
	public List<Vendedor> buscarTodos() {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			st = conn.prepareStatement(" SELECT vendedor.*, departamento.nome AS depNome "
					+ "FROM vendedor INNER JOIN departamento "
					+ "ON vendedor.departamentoid = departamento.id "
					+ "ORDER BY nome;");
			
			
			rs = st.executeQuery();//execução da query retornando para resultset
			
			List<Vendedor> list = new ArrayList<Vendedor>();
			Map<Integer, Departamento> map = new HashMap<Integer, Departamento>();
			
			while (rs.next()) { //condição de busca ate encontrar o ultimo dado no banco
				
				Departamento dep = map.get(rs.getInt("departamentoid"));
				
				if (dep == null) {
					
					dep = instanciacaoDepartamento(rs);
					map.put(rs.getInt("departamentoid"), dep);
				}
				
			//Metodo para chamada de departamento instanciado
							
				Vendedor obj = instanciacaoVendedor(rs, dep);
				
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
		//não há necessidade de fechar a conexão pois outro metodos podem utiliza-lá
	}
	}
	
	//Metodo que instancia um Vendedor (reuso)
	private Vendedor instanciacaoVendedor(ResultSet rs, Departamento dep) throws SQLException {
		
		Vendedor obj = new Vendedor();//instanciando um Objeto Vendedor
		obj.setId(rs.getInt("id"));//Setando a Variavel setId com o resultado vendedorId
		obj.setNome(rs.getString("nome"));//Setando a Variavel setNome com o resultado vendedorNome
		obj.setEmail(rs.getString("email"));//Setando a Variavel setEmail com o resultado vendedorEmail
		obj.setDataNascimento(new java.util.Date(rs.getTimestamp("dataNascimento").getTime()));
		obj.setSalario(rs.getDouble("salario"));
		obj.setDepartamento(dep);//Associando o objeto departamento ao vendedor
		
		return obj;
		
	}

	//Metodo que instancia um Departamento (reuso)
	private Departamento instanciacaoDepartamento(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();//instanciando um Objeto Departamento
		dep.setId(rs.getInt("departamentoid"));//Setando a Variavel setId com o resultado deparatamentoId
		dep.setNome(rs.getString("depNome")); //Setando a Variavel setNome com o resultado deparatamentoNome
		
		return dep;
	}

	@Override
	public List<Vendedor> buscarPorDepartamento(Departamento departamento) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			st = conn.prepareStatement(
					"SELECT vendedor.* "
					+ " ,departamento.nome as depNome "
					+ " FROM vendedor INNER JOIN departamento "
					+ "	ON vendedor.departamentoid = departamento.id "
					+ "	WHERE departamentoid = ? "
					+ "ORDER BY nome;");
			
			st.setInt(1,departamento.getId());//Entrada do vendedorId a pesquisar na consulta atraves "?"
			rs = st.executeQuery();//execução da query retornando para resultset
			
			List<Vendedor> list = new ArrayList<Vendedor>();
			Map<Integer, Departamento> map = new HashMap<Integer, Departamento>();
			
			while (rs.next()) { //condição de busca ate encontrar o ultimo dado no banco
				
				Departamento dep = map.get(rs.getInt("departamentoid"));
				
				if (dep == null) {
					
					dep = instanciacaoDepartamento(rs);
					map.put(rs.getInt("departamentoid"), dep);
				}
				
			//Metodo para chamada de departamento instanciado
							
				Vendedor obj = instanciacaoVendedor(rs, dep);
				
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
		//não há necessidade de fechar a conexão pois outro metodos podem utiliza-lá
	}
		
}


	
	/* SELECT vendedor.*,departamento.nome as DepName
FROM vendedor INNER JOIN departamento
ON vendedor.departamentoid = departamento.id
WHERE departamentoid = 1
ORDER BY nome;*/
	
}