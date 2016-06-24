package br.sharing.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.sharing.Criptografia;
import br.sharing.dao.IAlunoDAO;
import br.sharing.message.Mensagem;
import br.sharing.model.Aluno;
import br.sharing.model.Instituicao;

@Transactional
@Controller
@RequestMapping("/aluno")
public class AlunoController {

	@Autowired
	private IAlunoDAO alunoDao;
	
	@Autowired
	private InstituicaoController instituicaoController;
	
	@RequestMapping("/home")
	public String home(Model model) {
		return "/aluno/home";
	}
	
	@RequestMapping("/meuPerfil")
	public String meuPerfil(Model model, HttpSession sessao) {
		Aluno alunoSessao = (Aluno)sessao.getAttribute("aluno_logado");
		Aluno aluno = alunoDao.findByLoginLike(alunoSessao.getLogin());
		if (aluno != null) {
			model.addAttribute("aluno", aluno);
			return "/aluno/meu_perfil";
		} else {
			model.addAttribute("mensagem", Mensagem.N_ENCONTRADO);
			return "/erro";
		}
	}
	
	@RequestMapping("/formCadastrar")
	public String formCadastrar(Model model) {
		List<Instituicao> instituicoes = instituicaoController.getTodasInstituicoes();
		model.addAttribute("instituicoes", instituicoes);
		return "/aluno/form_cadastrar";
	}
	
	@RequestMapping("/cadastrar")
	public String cadastrar(Aluno candidato, @RequestParam("instituicao") Long idInstituicao, Model model) {
		Aluno aluno = alunoDao.findByLoginLike(candidato.getLogin());
		
		if (aluno == null) {
			candidato.setSenha(Criptografia.criptografar(candidato.getSenha()));
			candidato.setInstituicao(instituicaoController.getInstituicaoPorId(idInstituicao));
			alunoDao.save(candidato);
			return "redirect:/aluno/home";
		} else {
			model.addAttribute("mensagem", Mensagem.USUARIO_SENHA);
			return "/aluno/form_cadastrar";
		}
	}
	
	@RequestMapping("/alterarHorariosDisponiveis")
	public String alterarHorariosDisponiveis() {
		return "aluno/perfil/alterar_horarios_disponiveis";
	}
	
	@RequestMapping("/alterarDisciplinas")
	public String alterarDisciplinas() {
		return "aluno/perfil/alterar_disciplinas";
	}
}
