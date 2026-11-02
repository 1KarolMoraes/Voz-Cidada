# Regras de Git do grupo

App Android (Java + XML) com Firebase: Feed, Em Alta, Denunciar e Perfil.

## Equipe e branches

| # | Integrante | Responsabilidade | Branch |
|---|------------|------------------|--------|
| 1 | Angelo | Front-end (telas, navegação, componentes) | `feature/frontend` |
| 2 | Karol | Autenticação e usuários | `feature/autenticacao` |
| 3 | Renato | Dados, publicações e denúncias | `feature/publicacoes` |
| 4 | Frankson | Interações, Em Alta, testes e revisão de PRs | `feature/interacoes` |

## Branches principais

- `main`: versão estável. Ninguém faz push direto.
- `develop`: onde as features são integradas e testadas.
- `feature/*`: uma branch por integrante, criada a partir da `develop`.

## Configuração do repositório (feita uma vez pelo dono)

1. Adicionar os 4 integrantes em **Settings → Collaborators** (permissão Write).
2. Criar a branch `develop` a partir da `main`.
3. Proteger `main` e `develop` em **Settings → Branches**: exigir Pull Request e 1 aprovação antes do merge.

## Rotina de trabalho

```bash
# começar o dia
git checkout develop
git pull origin develop
git checkout feature/SUA-BRANCH
git merge develop

# trabalhar e enviar
git add .
git commit -m "feat: descrição curta do que foi feito"
git push
```

## Mensagens de commit

Use um prefixo e uma frase curta no imperativo:

- `feat:` nova funcionalidade
- `fix:` correção de bug
- `test:` testes
- `docs:` documentação
- `refactor:` reorganização de código sem mudar comportamento

Exemplo: `feat: adiciona botão de curtir na publicação`

## Pull Requests

1. Abra o PR da sua `feature/*` para a `develop`.
2. Descreva o que foi feito e como testar.
3. Marque pelo menos 1 colega para revisar.
4. Só faça o merge depois da aprovação e do app compilando.
5. Depois do merge, todos atualizam a branch com `git merge develop`.

### Checklist do revisor

- [ ] O app compila e abre no emulador
- [ ] O fluxo da tarefa funciona
- [ ] Não há senhas, chaves privadas ou `local.properties` no commit
- [ ] Os nomes de classes e arquivos seguem o padrão do grupo
- [ ] Não há código comentado ou arquivos de teste esquecidos

## Ordem de integração

1. `feature/frontend`
2. `feature/autenticacao`
3. `feature/publicacoes`
4. `feature/interacoes`

A branch de interações vem por último porque depende de usuários e publicações.

## Modelos de dados (combinar antes de codar)

Todos usam as mesmas classes Java: `Usuario`, `Publicacao` e `Comentario`.
Qualquer mudança nos campos deve ser avisada no grupo antes de ser commitada.

## O que nunca commitar

- `local.properties`
- Pastas `build/`, `.gradle/` e `.idea/`
- Chaves privadas e senhas
- `google-services.json`: o grupo decide em conjunto se entra ou não no repositório

## Conflitos de merge

1. Abra os arquivos marcados com `<<<<<<<`.
2. Escolha o trecho correto (ou combine os dois).
3. Rode `git add .` e `git commit`.
4. Em caso de dúvida, chame o autor do outro trecho antes de decidir.
