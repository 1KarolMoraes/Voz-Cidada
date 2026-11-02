# Plano de testes - Voz Cidadã

Responsável: Frankson (Integrante 4) | Branch: feature/interacoes

Legenda de status: [ ] pendente | [x] passou | [!] falhou

## 1. Cadastro, login e logout

| ID | Passos | Resultado esperado | Status |
|----|--------|--------------------|--------|
| C1 | Cadastrar com e-mail e senha válidos | Conta criada e usuário entra no app | [ ] |
| C2 | Cadastrar com e-mail já usado | Mensagem de erro, sem criar conta | [ ] |
| C3 | Cadastrar com senha curta | Mensagem de erro, sem criar conta | [ ] |
| C4 | Cadastrar com campos vazios | Mensagem de erro, sem travar | [ ] |
| C5 | Fazer login com dados corretos | Entra no Feed | [ ] |
| C6 | Fazer login com senha errada | Mensagem de erro | [ ] |
| C7 | Fechar e reabrir o app logado | Sessão continua ativa | [ ] |
| C8 | Fazer logout | Volta para a tela de login | [ ] |

## 2. Publicação

| ID | Passos | Resultado esperado | Status |
|----|--------|--------------------|--------|
| P1 | Criar publicação com descrição e categoria | Aparece no Feed | [ ] |
| P2 | Criar publicação com imagem | Imagem aparece na publicação | [ ] |
| P3 | Criar publicação sem descrição | Mensagem de erro | [ ] |
| P4 | Criar publicação sem estar logado | Pede login | [ ] |
| P5 | Conferir autor e localização | Dados corretos na publicação | [ ] |

## 3. Navegação

| ID | Passos | Resultado esperado | Status |
|----|--------|--------------------|--------|
| N1 | Abrir Feed, Em Alta, Denunciar e Perfil em sequência | Todas abrem sem fechar o app | [ ] |
| N2 | Botão voltar em cada tela | Volta para a tela anterior | [ ] |
| N3 | Girar a tela do celular | Tela mantém os dados | [ ] |
| N4 | Abrir o app sem internet | Mensagem clara, sem travar | [ ] |

## 4. Curtidas e comentários

| ID | Passos | Resultado esperado | Status |
|----|--------|--------------------|--------|
| I1 | Curtir uma publicação | Contador sobe 1 | [ ] |
| I2 | Curtir de novo a mesma publicação | Curtida some e contador desce 1 | [ ] |
| I3 | Tocar várias vezes seguidas no botão curtir | Contador fica correto | [ ] |
| I4 | Comentar com texto válido | Comentário aparece e contador sobe 1 | [ ] |
| I5 | Comentar com texto vazio | Não envia | [ ] |
| I6 | Comentar sem estar logado | Pede login | [ ] |

## 5. Em Alta

| ID | Passos | Resultado esperado | Status |
|----|--------|--------------------|--------|
| E1 | Curtir e comentar uma publicação nova | Sobe na lista Em Alta | [ ] |
| E2 | Comparar publicação nova e antiga com mesma interação | A nova fica na frente | [ ] |
| E3 | Publicação com mais de 7 dias | Não aparece no Em Alta | [ ] |

## Testes unitários

- CriterioEmAltaTest: 6 testes sobre a pontuação e a janela de 7 dias.

## Bugs encontrados

| ID do teste | O que aconteceu | Quem corrige |
|-------------|-----------------|--------------|
|             |                 |              |
