# AVATAR: O Despertar dos Quatro Elementos (Minecraft 1.21.1 · Fabric)

Só precisa do **Fabric API**. Funciona em servidor dedicado e multiplayer.

## Primeiros passos
1. Ao entrar no mundo pela primeira vez abre a tela **Escolha seu elemento** (Fogo, Água, Terra ou Ar).
2. Teclas (mudáveis em Controles > Avatar):
   - **Z X C V B N M** = as 7 técnicas do seu elemento
   - **G** = técnica especial selecionada (segure para carregar), **R** = próxima especial
   - **H** = Estado Avatar, **Y** = trocar de elemento (no Estado Avatar), **U** = menu de progresso
3. Os rangs: Aprendiz, Dobrador, Especialista, Mestre, Grande Mestre, Lenda. Usar poderes dá domínio (XP).

## Técnicas secretas (desafios)
Raios (20 raios) · Combustão (20 mortes com Fogo) · Dobra de Sangue (20 mortes com Água, só à noite) · Cura Avançada (200 de vida curada) · Estilhaços de Metal (15 usos de Dobra de Metal) · Lava (30 mortes com Terra) · Sismologia (10 terremotos) · Projeção Espiritual (2400 ticks de voo) · Controle de Pressão (25 mortes com Ar). Também dá para usar o **Pergaminho Antigo** (+300 de domínio e a próxima técnica secreta).

## Estado Avatar
Chegue ao rank **Lenda** e use a **Gema Espiritual**. Aperte H: os quatro elementos orbitam você, técnicas mais fortes e baratas, e libera as combinações (Tornado Flamejante, Tempestade, Erupção Vulcânica, Prisão de Lama, Explosão de Vapor) e o **Colapso Elemental**.

## Itens
- Pergaminho Antigo: lápis, papel, diamante (`LPL/PDP/LPL`)
- Gema Espiritual: olho do fim, bloco de diamante, estrela do Nether (`EDE/DND/EDE`)

## Comandos (op)
`/avatar element <fire|water|earth|air>` · `/avatar choose` · `/avatar rank <0-5>` · `/avatar master` · `/avatar unlock` · `/avatar avatar` · `/avatar energy <n>` · `/avatar reset`

## Configuração
`config/avatar.json`: multiplicadores de custo/recarga/dano/energia, `allowPvp`, `modifyBlocks` (false = nenhum bloco é alterado), `restoreTicks` (blocos voltam ao normal), `craters`, `fireIgnites`, `spawnProtectionRadius`, `maxBlocksPerCast`, `particleDensity`, `cameraShake`, `secretsPermissionLevel`, `avatarStatePermissionLevel`.
