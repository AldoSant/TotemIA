package com.totem.ia.data

import com.totem.ia.domain.model.Chapter
import com.totem.ia.domain.model.Journey

object MockJourneyData {
    val MOCK_JOURNEYS = listOf(

        Journey(
            id = "journey_1",
            title = "Mindset de Crescimento para Empreendedores e Líderes",
            category = "Psicologia",
            description = "sair de um mindset fixo (provar valor, medo de errar) para um mindset de crescimento aplicado a negócios, liderança e hábitos diários.",
            durationType = "capítulos",
            chapters = listOf(

                Chapter(
                    id = "chapter_1",
                    journeyId = "journey_1",
                    order = 1,
                    title = "Os Dois Mindsets Definem Seu Jogo",
                    objective = "entender claramente a diferença entre mindset fixo e de crescimento e como isso se manifesta no dia a dia de negócios.",
                    scriptBase = "Conteúdo base (bullets):\nDefinição de **Mindset Fixo**: inteligência/talento como algo imutável, medo de errar, evitar desafios.\nDefinição de **Mindset de Crescimento**: habilidades desenvolvíveis com esforço, aprendizado e persistência.\nExemplos de empresas: culturas obcecadas por “talento nato” que colapsam vs. culturas de aprendizado que prosperam.\n“Em qual situações você se pega tentando provar que é bom, em vez de aprender?”",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_2",
                    journeyId = "journey_1",
                    order = 2,
                    title = "Esforço, Fracasso e Talento",
                    objective = "ressignificar esforço e fracasso como partes do processo, não sinais de incapacidade.",
                    scriptBase = "Conteúdo base:\nEsforço como **estratégia**, não fraqueza (“se eu fosse bom, seria fácil” vs. “esforço me torna bom”).\nFracasso como **informação**, não identidade (história do Jim Marshall).\nTalento como **ponto de partida, não teto** (Darwin, Tolstói, Ben Hogan etc.).\n“Qual grande fracasso recente ainda está colado na sua identidade?”",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_3",
                    journeyId = "journey_1",
                    order = 3,
                    title = "Doença do CEO vs. Líder que Aprende",
                    objective = "reconhecer padrões de liderança de mindset fixo e de crescimento em você e na empresa.",
                    scriptBase = "Conteúdo base:\nCaso Lee Iacocca: cercar‑se de bajuladores, recusar críticas, repetir modelos → declínio.\nCaso Alan Wurtzel (Circuit City): perguntar “por quê”, aceitar feedback brutal, aprender com dados → retorno extraordinário aos acionistas.\n“Em que pontos da sua liderança você está mais parecido com Lee do que com Wurtzel?”\nPerguntar para 1 liderado de confiança: “O que eu mais faço que te impede de dar o seu melhor?” (só ouvir e agradecer).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_4",
                    journeyId = "journey_1",
                    order = 4,
                    title = "Do Gênio Solitário à Equipe que Cresce",
                    objective = "trocar o modelo “gênio + mil ajudantes” por um modelo de equipe que aprende junto.",
                    scriptBase = "Conteúdo base:\nCrítica ao modelo de “gênio no topo” e por que ele limita a empresa.\nPapéis de um líder de crescimento: fazer a sala inteira ficar mais inteligente.\n“Você estrutura sua empresa para depender de você ou para crescer sem você?”\nIdentificar 1 decisão importante por semana que você poderia transformar em discussão de aprendizado com a equipe, em vez de decidir sozinho.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_5",
                    journeyId = "journey_1",
                    order = 5,
                    title = "Contratar pelo Potencial, Não Só pelo CV",
                    objective = "ajustar o modo como você avalia pessoas (contratação e promoção) para privilegiar mindset de crescimento.",
                    scriptBase = "Conteúdo base:\nCaso NASA e Jack Welch: preferência por candidatos com histórico de fracasso + recuperação.\nPerguntas de entrevista focadas em erros, aprendizado e responsabilidade.\n“Quantas pessoas talentosas você já descartou porque ‘fracassaram’ em algo?”\nAtualizar seu roteiro de entrevista com pelo menos 3 perguntas de mindset de crescimento.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_6",
                    journeyId = "journey_1",
                    order = 6,
                    title = "Reuniões como Laboratório de Aprendizado",
                    objective = "transformar reuniões de teatro em sessões de aprendizado real.",
                    scriptBase = "Conteúdo base:\nExemplo de Wurtzel trocando apresentações por debates honestos.\nFrases‑chave: “O que estamos errando?” em vez de “O que estamos acertando?”.\n“Quantas reuniões suas servem para impressionar e quantas para aprender?”\nNa próxima reunião, abrir com a pergunta: “Qual verdade estamos evitando olhar?” e registrar as respostas.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_7",
                    journeyId = "journey_1",
                    order = 7,
                    title = "Feedback como GPS, Não Sentença",
                    objective = "ensinar o usuário a usar o script de feedback de crescimento.",
                    scriptBase = "Conteúdo base:\nDiferença entre feedback que julga (“você é assim”) e feedback que orienta (“aqui você pode melhorar”).\nScript de 4 passos: especificidade, impacto, caminho, confiança.\n“Qual foi o último feedback que você deu que soou como sentença, não como ajuda?”\nReescrever um feedback recente usando o script de 4 passos e aplicá‑lo com a pessoa.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_8",
                    journeyId = "journey_1",
                    order = 8,
                    title = "O Poder do ‘Ainda Não’",
                    objective = "ensinar a usar “ainda não” para transformar bloqueios em projetos.",
                    scriptBase = "Conteúdo base:\nFramework “Ainda Não” para desafios (exemplos de reescrita de frases).\n“Qual frase fixa sobre você/empresa mais te trava hoje?”\nTransformar 3 frases fixas em versões “ainda não” com um micro‑experimento associado.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_9",
                    journeyId = "journey_1",
                    order = 9,
                    title = "Ritual do Fracasso da Semana",
                    objective = "normalizar o erro como combustível de aprendizado.",
                    scriptBase = "Conteúdo base:\nRitual semanal: cada pessoa compartilha 1 erro, 1 aprendizado, 1 teste futuro; a equipe aplaude.\n“Quanta vergonha existe hoje na sua equipe em admitir erro?”\nRodar um piloto de “Fracasso da Semana” na próxima reunião, começando por você.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_10",
                    journeyId = "journey_1",
                    order = 10,
                    title = "Plano de Ação em 3 Níveis",
                    objective = "conectar o conteúdo todo em um plano concreto de 24h, 7 dias e 30–90 dias.[^1]",
                    scriptBase = "Conteúdo base:\nAções de Nível 1 (hoje): identificar mindset dominante, reformular crença, pedir 1 feedback.\nAções de Nível 2 (semana): reunião de verdade brutal, ritual de fracasso, elogios de crescimento.\nAções de Nível 3 (30–90 dias): workshop, redesign de contratação, OKRs de aprendizado, journaling.\n“Se você tivesse que escolher só 1 ação de cada nível, quais seriam?”",
                    estimatedDurationMin = 15
                ),
            )
        ),
        Journey(
            id = "journey_2",
            title = "Ikigai: Propósito, Energia e Longevidade para Empreendedores",
            category = "Filosofia",
            description = "sair de uma rotina sem sentido (só apagar incêndio e buscar dinheiro) para uma vida com propósito claro, energia sustentável e decisões alinhadas ao seu “porquê”.",
            durationType = "capítulos",
            chapters = listOf(

                Chapter(
                    id = "chapter_11",
                    journeyId = "journey_2",
                    order = 11,
                    title = "O que é Ikigai de Verdade",
                    objective = "entender Ikigai como razão de ser, não só “trabalho dos sonhos”.",
                    scriptBase = "Ikigai = interseção entre: o que você ama, no que é bom, o que o mundo precisa, e pelo que pode ser pago.\nCentenários de Okinawa não separam “trabalho” de “vida”; têm sempre algo a fazer, amar e esperar.\nIkigai como motor de energia, resiliência e longevidade.\n“Hoje você acorda mais por obrigação ou por uma razão que te anima?”\nEscrever, em 3 linhas, qual seria a sua “razão para acordar” se dinheiro não fosse problema.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_12",
                    journeyId = "journey_2",
                    order = 12,
                    title = "Regras de Longevidade: 80%, Flow e Nunca se Aposentar",
                    objective = "introduzir princípios práticos de longevidade ligados ao ikigai.",
                    scriptBase = "Hara Hachi Bu: comer até 80% da saciedade, e aplicar o “80%” também à agenda (não lotá‑la).\nEstado de flow: foco profundo numa atividade desafiadora, clara e sem distrações.\nIdeia de “nunca se aposentar mentalmente”: manter‑se ativo física e mentalmente.\n“Sua agenda está mais para 120% lotada ou 80% com espaço para respirar?”\nEscolher 1 refeição do dia e praticar conscientemente o Hara Hachi Bu (parar com 80% de saciedade).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_13",
                    journeyId = "journey_2",
                    order = 13,
                    title = "Ikigai como Bússola de Negócios",
                    objective = "usar ikigai como critério para decisões de negócio.",
                    scriptBase = "Bússola vs mapa: é melhor saber a direção certa do que ter um mapa detalhado sem rumo.\nRevisar decisões à luz de um objetivo estratégico claro por trimestre.\nEliminar a “neurose de domingo” redesenhando o negócio sob a ótica do ikigai.\n“Seu negócio, hoje, te leva para o que você quer sentir/viver ou só paga boletos?”\nDefinir 1 objetivo estratégico trimestral ligado ao seu ikigai e listar 3 ações da semana que o aproximam disso.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_14",
                    journeyId = "journey_2",
                    order = 14,
                    title = "Moai: Comunidade como Superpoder",
                    objective = "mostrar o papel de comunidade e pertencimento na performance e saúde mental.",
                    scriptBase = "Conceito de Moai: grupos de apoio de longo prazo em Okinawa, amigos para a vida.\nComunidade reduz estresse e solidão; solidão é tão letal quanto fumar 15 cigarros por dia.\nAdaptação para empresas: moais corporativos, células de 5–8 pessoas com apoio mútuo.\n“Hoje, quem são as 3 pessoas com quem você pode falar abertamente sobre vida e negócios?”\nEnviar 1 mensagem para 2 pessoas convidando‑as para um encontro recorrente de apoio (virtual ou presencial).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_15",
                    journeyId = "journey_2",
                    order = 15,
                    title = "Antifragilidade: Ficar Melhor com os Tombos",
                    objective = "aplicar antifragilidade à vida financeira, à carreira e à empresa.",
                    scriptBase = "Resiliência vs antifragilidade: não só voltar ao normal, mas sair mais forte.\nEstratégia da barra (barbell): 90% seguro + 10% apostas arriscadas.\nVia negativa: eliminar o que te torna frágil (dívidas ruins, pessoas tóxicas, compromissos vazios).\n“Hoje, onde você está excessivamente exposto em 1 único ponto de falha (um cliente, um emprego, um sócio)?”\nListar 3 fragilidades (financeiras, de tempo, de relacionamentos) e escolher 1 para reduzir nos próximos 7 dias.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_16",
                    journeyId = "journey_2",
                    order = 16,
                    title = "Energia Primeiro: Sono, Agenda e Prioridades 1‑3‑5",
                    objective = "alinhar gestão de energia com o propósito.",
                    scriptBase = "Sono de 7–9 horas, melatonina como “antioxidante natural de juventude”.\nExposição ao sol, evitar álcool/cafeína antes de dormir, rotina de sono regular.\nTécnica 1‑3‑5: 1 tarefa grande, 3 médias, 5 pequenas; a tarefa grande conectada ao ikigai.\n“Sua rotina atual te dá energia para perseguir seu ikigai ou te drena?”\nPlanejar o dia seguinte com 1 tarefa grande conectada ao ikigai, 3 médias e 5 pequenas.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_17",
                    journeyId = "journey_2",
                    order = 17,
                    title = "Mindfulness Aplicado ao Dia Comum",
                    objective = "usar atenção plena em micro‑momentos, reduzindo ansiedade.",
                    scriptBase = "Mindfulness não é só meditação; é presença em atividades simples (acordar, preparar café, caminhar, esperar em filas).\nBenefícios: redução de ansiedade, mais clareza e decisões melhores.\n“Qual parte da sua rotina diária é tão automática que você nem percebe que vive?”\nEscolher 3 momentos do dia (ex.: café da manhã, caminhada, banho) e praticar atenção plena em cada um.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_18",
                    journeyId = "journey_2",
                    order = 18,
                    title = "Multitarefa é a Nova Fumaça: O Custo Oculto",
                    objective = "mostrar cientificamente o custo da multitarefa e propor foco profundo.",
                    scriptBase = "Estudos de Stanford: multitarefa reduz produtividade e aumenta erros; vicia o cérebro em estímulo constante.\nEfeitos em adolescentes e adultos: menos sono, mais depressão, menos integração social.\nProtocolo: 90–120 min bloqueados para 1 tarefa crítica, celular desligado, equipe avisada.\n“Se você tivesse só 90 minutos de trabalho por dia, em que colocaria essa janela?”\nAgendar um bloco de 60–90 min de foco absoluto amanhã para a tarefa mais ligada ao seu ikigai.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_19",
                    journeyId = "journey_2",
                    order = 19,
                    title = "Rotinas dos Centenários: Calma, Movimento e Simplicidade",
                    objective = "inspirar uma rotina diária mais sustentável e alinhada ao propósito.",
                    scriptBase = "Rotina matinal dos centenários: acordar cedo, gratidão, alongamentos, café leve, caminhada.\nTrabalho com as mãos, sociabilidade diária, jantar leve e dormir cedo.\nIdeia central: vida ocupada, mas sem pressa; ritmo calmo porém produtivo.\n“O que na sua rotina hoje é ‘correria’ que poderia virar ‘cadência’?”\nImplementar 1 elemento da rotina dos centenários (ex.: caminhada diária, alongamento matinal ou jantar mais leve).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_20",
                    journeyId = "journey_2",
                    order = 20,
                    title = "Erros Comuns: Paixão, Perfeccionismo e Trabalho-Único",
                    objective = "evitar interpretações tóxicas do conceito de ikigai.",
                    scriptBase = "Único”\nConfundir ikigai com “seguir sua paixão” intensa que queima rápido.\nBuscar um ikigai perfeito antes de agir → paralisia por análise.\nReduzir ikigai só a trabalho, e perder o sentido ao se aposentar.\nForçar flow em tudo; usar wabi‑sabi como desculpa para mediocridade; ignorar o corpo focando só na mente.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_21",
                    journeyId = "journey_2",
                    order = 21,
                    title = "Framework de Descoberta do Ikigai (4 Círculos)",
                    objective = "conduzir o usuário pelo diagrama de Venn do ikigai.",
                    scriptBase = "Quatro perguntas: o que você ama, no que é excelente, pelo que pode ser pago, o que o mundo precisa.\nExplicação das interseções (alegria, conforto, utilidade, satisfação) e do centro do ikigai.\nChecklist rápido de validação: faria de graça, é top 10%, alguém paga bem, resolve problema real.\n“O que aparece repetidamente quando você preenche esses quatro círculos?”\nListar 10 itens em cada círculo e marcar as interseções mais fortes; escolher 1 hipótese de ikigai profissional para testar por 90 dias.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_22",
                    journeyId = "journey_2",
                    order = 22,
                    title = "Plano de Ação em 3 Níveis (Hoje, Semana, 30–90 dias)",
                    objective = "transformar tudo em ações pequenas e grandes.",
                    scriptBase = "Ações para HOJE: sentir “gostinho” de ikigai e flow imediatamente.\nAções para esta SEMANA: ajustar agenda (hara hachi bu na agenda, flow sprints), praticar jejum de tecnologia, revisar decisões à luz do ikigai.\nAções para 30–90 DIAS: testar 3 hipóteses de ikigai, construir moai, criar rotina de energia e foco sustentáveis.\n“Se você só pudesse executar 1 ação hoje, 1 nesta semana e 1 nos próximos 90 dias, quais seriam?”\nEscolher e agendar: 1 ação imediata, 1 semanal, 1 de 30–90 dias, todas conectadas ao ikigai escolhido.",
                    estimatedDurationMin = 15
                ),
            )
        ),
        Journey(
            id = "journey_3",
            title = "O Alquimista: Lenda Pessoal para Empreendedores",
            category = "Livro",
            description = "sair da vida “OK, mas vazia” (zona de conforto, medo, adiamento) para viver a própria Lenda Pessoal em negócios, projetos e decisões.",
            durationType = "capítulos",
            chapters = listOf(

                Chapter(
                    id = "chapter_23",
                    journeyId = "journey_3",
                    order = 23,
                    title = "O que é a Sua Lenda Pessoal?",
                    objective = "ajudar o usuário a identificar o sonho central da vida dele.",
                    scriptBase = "Lenda Pessoal = o sonho profundo que você tem medo de admitir, mas não consegue esquecer.\nPerguntas-chave: o que faria se tivesse certeza de não fracassar; o que fazia na infância que te deixava absorto; que arrependimento você não quer ter em 20 anos.\n“Se nada pudesse dar errado, o que você escolheria construir nos próximos 10 anos?”\nResponder por escrito às 5 perguntas do exercício da Lenda Pessoal e destacar o padrão que mais se repete.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_24",
                    journeyId = "journey_3",
                    order = 24,
                    title = "Zona de Conforto: Seu Rebanho Invisível",
                    objective = "mostrar onde o usuário está “pastorando o rebanho” em vez de ir às Pirâmides.",
                    scriptBase = "Santiago deixa o rebanho (segurança) para ir atrás de um sonho incerto.\nNo mundo real: emprego seguro que não leva a lugar nenhum; empreendedor preso no operacional; líder acomodado que não assume desafios.\n“Em qual área da sua vida você está escolhendo segurança em vez de Lenda Pessoal?”\nDefinir 1 movimento concreto de saída de zona de conforto (ex.: iniciar projeto paralelo, delegar parte do operacional, aceitar um desafio que assusta) com prazo.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_25",
                    journeyId = "journey_3",
                    order = 25,
                    title = "Sinais e Linguagem do Mundo",
                    objective = "treinar o usuário a perceber sinais do mercado, do time e da própria intuição.",
                    scriptBase = "Sinais como intuição, coincidências, feedbacks sutis do mercado.\nNa liderança: notar mudanças pequenas antes de virarem crises; escutar o que o time não fala; perceber quando um projeto pede para morrer ou nascer.\n“Que sinais você tem ignorado sobre seu negócio, seu corpo ou seus relacionamentos?”\nComeçar um “Diário de Sinais” com 3 colunas: o que aconteceu, o que isso pode estar dizendo, que decisão preciso tomar.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_26",
                    journeyId = "journey_3",
                    order = 26,
                    title = "O Medo é Pior que o Sofrimento",
                    objective = "ressignificar o medo que paralisa decisões importantes.",
                    scriptBase = "Santiago quase desiste várias vezes por medo de perder o que já tem, de sofrer, de não ser digno.\nPara empreendedores: medo de fracassar impede lançar; medo de julgamento impede inovar; medo de perder segurança prende em trabalho que odeia.\n“Qual projeto ou decisão você está adiando só por medo do que pode acontecer?”\nEscrever 2 listas: “Medos se eu agir” vs. “Consequências se eu não agir em 5 anos” e comparar.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_27",
                    journeyId = "journey_3",
                    order = 27,
                    title = "A Prova do Conquistador",
                    objective = "ensinar a reconhecer e atravessar a fase em que tudo desmorona perto da vitória.",
                    scriptBase = "Antes de realizar o sonho, vem a prova mais dura; tudo parece desmoronar no final.\nMétodo de decisão: pausar 24h, perguntar se está desistindo porque ficou difícil ou porque o caminho está errado.\n“Você já abandonou algo justamente quando as coisas ficaram mais difíceis?”\nEscolher 1 projeto atual e aplicar o “Teste de Coragem/Prova do Conquistador”: pausar, responder às perguntas, decidir conscientemente se persiste ou pivota.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_28",
                    journeyId = "journey_3",
                    order = 28,
                    title = "Ouça o Coração, mas Seja o Líder",
                    objective = "equilibrar emoção e razão em decisões difíceis.",
                    scriptBase = "Conversa de Santiago com o coração: ele teme para nos proteger, mas quem decide é você.\nExemplos de liderança: não manter funcionário por apego emocional quando ele destrói o time; ajustar preços mesmo com medo de perder clientes.\n“Em qual decisão recente você deixou o coração mandar sozinho e se arrependeu?”\nEscolher 1 decisão pendente e listar “o que o coração quer” vs. “o que a razão vê” e escrever uma terceira opção que respeite ambos.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_29",
                    journeyId = "journey_3",
                    order = 29,
                    title = "Mercador de Cristais: Prisão do ‘E Se Eu Fracassar?’",
                    objective = "libertar o usuário do apego ao sonho não vivido.",
                    scriptBase = "Mercador que sonhava ir a Meca, mas preferiu nunca ir para não se decepcionar.\nNa vida real: pessoas que preferem sonhar com um negócio do que realmente testá‑lo.\n“Qual é a sua ‘Meca’ que você mantém como sonho porque tem medo de testar de verdade?”\nDefinir 1 micro‑teste concreto para esse sonho (MVP, conversa, protótipo, viagem exploratória) com prazo definido.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_30",
                    journeyId = "journey_3",
                    order = 30,
                    title = "O Alquimista vs. O Inglês: Teoria x Prática",
                    objective = "criar critério para escolher mentores, parceiros e contratações.",
                    scriptBase = "O Inglês: muita teoria, pouca prática.\nO Alquimista: vive os princípios e produz resultados concretos.\nNa contratação e parcerias: valorizar quem faz e entrega, não só quem fala bonito e tem diploma.\n“Quem na sua vida atual é ‘Inglês’ (fala muito, entrega pouco) e quem é ‘Alquimista’ (entrega em silêncio)?”\nRevisar 1 relação (sócio, fornecedor, mentor) e definir um próximo passo: aproximar mais um ‘Alquimista’ ou reduzir espaço de um ‘Inglês’.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_31",
                    journeyId = "journey_3",
                    order = 31,
                    title = "Exercício Profundo: Descubra Sua Lenda Pessoal",
                    objective = "consolidar a visão de Lenda Pessoal com as perguntas do guia.",
                    scriptBase = "Repetir e aprofundar as 5 perguntas: não fracassar, infância, 10 anos garantidos, se dinheiro não fosse problema, arrependimentos futuros.\nOrientação: responder sem filtro, rápido, deixando o inconsciente falar.\n“O que mais te assusta nessas respostas? Normalmente é isso que mais importa.”\nEscolher 3 frases dessas respostas que melhor resumem sua Lenda Pessoal atual e guardá‑las como referência no app.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_32",
                    journeyId = "journey_3",
                    order = 32,
                    title = "Diário de Sinais e Decisões",
                    objective = "transformar leitura de sinais em prática semanal.",
                    scriptBase = "Estrutura do Diário de Sinais: o que aconteceu; o que isso pode significar; que decisão tomar.\nRevisão semanal para identificar padrões e ajustar rota.\n“Se você tivesse acompanhado seus sinais nos últimos 6 meses, onde já poderia estar?”\nDefinir 1 horário fixo semanal (ex.: domingo à noite) para revisar o Diário de Sinais e tomar 1 decisão baseada nele.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_33",
                    journeyId = "journey_3",
                    order = 33,
                    title = "Plano de Coragem Contínua",
                    objective = "criar um sistema para não abandonar a Lenda Pessoal ao primeiro obstáculo.",
                    scriptBase = "Método de pausa de 24h antes de desistir; perguntas para diferenciar dificuldade de caminho errado.\nUso simbólico do “Urim e Tumim”/moeda: observar a reação interna ao resultado (alívio ou frustração).\n“O que você precisa anotar hoje para se lembrar, no próximo momento de crise, de não se sabotar?”\nEscrever um “Manifesto de Coragem” de 5 linhas para você mesmo, para ser lido quando pensar em desistir de algo importante.\nlivros.pdf",
                    estimatedDurationMin = 15
                ),
            )
        ),
        Journey(
            id = "journey_4",
            title = "Do Zero ao Novo Rico em 90 Dias",
            category = "Comportamento",
            description = "sair da corrida dos ratos (sem tempo, sem liberdade) para um estilo de vida com tempo, renda mais automatizada e mini‑aposentadorias planejadas.",
            durationType = "capítulos",
            chapters = listOf(

                Chapter(
                    id = "chapter_34",
                    journeyId = "journey_4",
                    order = 34,
                    title = "Defina o Medo, Não Só a Meta",
                    objective = "dissolver a nebulosidade do medo para destravar ação.",
                    scriptBase = "Exercício “Definir o Medo”: o que de pior pode acontecer, como prevenir, como reparar.\nMedo mal definido = paralisia; medo escrito = projeto de mitigação.\n“Qual é o cenário que você mais teme se mudar seu trabalho/negócio hoje?”\nFazer a tabela de Definição do Medo para 1 mudança importante (ex.: pedir remoto, lançar musa, largar cliente ruim).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_35",
                    journeyId = "journey_4",
                    order = 35,
                    title = "RMN e Sonhos Concretos",
                    objective = "descobrir quanto você REALMENTE precisa por mês e quais sonhos vai financiar.",
                    scriptBase = "Renda Mensal Necessária (RMN): calcular custos de vida e sonhos dos próximos 6–12 meses.\nTransformar “quero ser rico” em sonhos específicos com custo, prazo e ações.\n“Você está perseguindo um número abstrato ou um conjunto de vidas concretas?”\nCalcular a própria RMN e listar 3 sonhos com valor, prazo e primeira ação.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_36",
                    journeyId = "journey_4",
                    order = 36,
                    title = "Dieta de Informação e Não",
                    objective = "reduzir ruído (notícias, redes, inputs).",
                    scriptBase = "Dieta de informação: zero notícias, email 2x/dia, cortar consumo passivo sem propósito.\nDizer “não” para 80% dos pedidos e convites que não se conectam ao plano.\n“O que você consome todo dia que não muda em nada sua vida 3 meses depois?”\nEscolher 1 fonte de ruído (portal, rede, grupo) para silenciar/bloquear pelos próximos 7 dias.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_37",
                    journeyId = "journey_4",
                    order = 37,
                    title = "Matando Reuniões e Tarefas Vampiras",
                    objective = "cortar reuniões inúteis e tarefas que não geram resultado.",
                    scriptBase = "Cancelar ou encurtar reuniões sem pauta, sem decisão clara.\nAplicar 80/20 em tarefas: 20% geram 80% dos resultados; 80% podem ser eliminadas, delegadas ou automatizadas.\n“Se você fosse obrigado a trabalhar só 2 horas por dia, o que continuaria fazendo?”\nMarcar 3 atividades da semana como “vampiras” (pouco retorno) e decidir: eliminar, delegar ou automatizar.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_38",
                    journeyId = "journey_4",
                    order = 38,
                    title = "Assistente Virtual e Sistema Pessoal",
                    objective = "tirar você do operacional básico via AV e processos.",
                    scriptBase = "Teste de Assistente Virtual com pequeno orçamento (ex.: 50 dólares) para tarefas administrativas.\nCriar “manual de operações” para tarefas repetitivas.\n“Qual tarefa você faz toda semana que qualquer pessoa treinada poderia assumir?”\nEscolher 1 tarefa para delegar a um AV (real ou futuro) e escrever passo a passo como se estivesse treinando essa pessoa.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_39",
                    journeyId = "journey_4",
                    order = 39,
                    title = "Musas: Ideias que Podem Pagar Sua Liberdade",
                    objective = "gerar e testar ideias de produtos/serviços com potencial de renda semi‑automática.",
                    scriptBase = "Identificar 5 nichos com problemas específicos; criar 3 produtos potenciais (ticket médio).\nTestar cada ideia com landing page + anúncios de baixo custo; medir tração.\n“Em quais problemas as pessoas já te pedem ajuda espontaneamente?”\nListar 5 nichos + 3 ideias de produto, e escolher 1 para criar uma landing page simples e validar.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_40",
                    journeyId = "journey_4",
                    order = 40,
                    title = "Negociando Liberdade (Remoto e Saída do Operacional)",
                    objective = "construir mais liberdade geográfica e de agenda.",
                    scriptBase = "Negociar trabalho remoto progressivo (1 dia, depois 2–3 dias).\nTestar o negócio funcionando sem você por uma semana, com tudo documentado e delegado.\n“O que hoje está nas suas mãos que necessariamente deveria ser de outra pessoa?”\nDesenhar um experimento de 1 semana em que você se afasta parcialmente do operacional e medir o que quebra (para corrigir).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_41",
                    journeyId = "journey_4",
                    order = 41,
                    title = "Mini‑aposentadorias e Vida de Novo Rico",
                    objective = "quebrar a lógica “trabalhar a vida toda para aproveitar no fim” e trazer prazer para agora.",
                    scriptBase = "Mini‑aposentadorias de 2–4 semanas em lugares com custo baixo, enquanto o trabalho/negócio segue funcionando.\nGeoarbitragem: ganhar em moeda forte, gastar em moeda fraca, aumentar qualidade de vida.\nNovo normal: 4–15h/semana de trabalho, múltiplas fontes de renda automatizada, lugar de moradia flexível.\n“Se você tivesse 1 mês livre pago agora, onde iria e como viveria?”\nEscolher 1 mini‑aposentadoria (prazo, lugar e custo aproximado) e definir 1 requisito mínimo que precisa estar funcionando para isso ser possível.",
                    estimatedDurationMin = 15
                ),
            )
        ),
        Journey(
            id = "journey_5",
            title = "Ferramentas dos Titãs: Sistema Pessoal de Alta Performance",
            category = "Comportamento",
            description = "sair de produtividade caótica e hábitos aleatórios para um sistema de rotina, energia e foco alinhado com objetivos grandes.",
            durationType = "capítulos",
            chapters = listOf(

                Chapter(
                    id = "chapter_42",
                    journeyId = "journey_5",
                    order = 42,
                    title = "Manhã de Titã: Começo de Dia que Manda no Resto",
                    objective = "ensinar uma rotina matinal mínima de alta performance.",
                    scriptBase = "Bloco 6h–7h30 (ou similar): planejar metas, definir 3 prioridades, fazer trabalho profundo antes de checar e‑mail.\nRegra: nunca checar e‑mail antes de fazer a tarefa de maior impacto.\n“Hoje, quem manda no seu dia: você ou a caixa de entrada?”\nEscolher 1 manhã na semana para testar uma rotina “titã” (planejamento + 1h de trabalho profundo sem e‑mail).",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_43",
                    journeyId = "journey_5",
                    order = 43,
                    title = "Zonas de Energia: Verde, Amarela e Vermelha",
                    objective = "casar tipo de tarefa com nível de energia do dia.",
                    scriptBase = "Zonas: verde (alta energia, manhã), amarela (média), vermelha (baixa, fim do dia).\nColocar decisões estratégicas e criação na zona verde, reuniões na amarela, tarefas mecânicas na vermelha.\n“Você está usando sua melhor energia para o que é mais importante ou para o urgente dos outros?”\nMapear seu dia de ontem em verde/amarelo/vermelho e listar 1 ajuste simples para amanhã.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_44",
                    journeyId = "journey_5",
                    order = 44,
                    title = "Blocos de 90 Minutos: A Técnica dos Monges",
                    objective = "introduzir a técnica de foco concentrado com pausas planejadas.",
                    scriptBase = "Setup: fechar tudo, celular fora de vista, timer de 90min, respiração.\nDurante: anotar distrações em papel, não ceder; pausa de 15min depois com movimento.\nMeta: 2–3 blocos/dia resolvem 80–100% do trabalho importante.\n“Quando foi a última vez que você passou 90min sem nenhuma distração em UMA coisa só?”\nAgendar 1 bloco de 60–90min de foco extremo nesta semana para uma tarefa de alto impacto.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_45",
                    journeyId = "journey_5",
                    order = 45,
                    title = "Pequenas Vitórias e Revisão Semanal",
                    objective = "treinar o cérebro a focar em progresso e ajustar rumo.",
                    scriptBase = "Diário de Pequenas Vitórias: todos os dias, 3 coisas que foram bem.\nRevisão semanal obsessiva: o que funcionou/ não funcionou / o que ajustar.\nRegra “2 dias nunca”: pode falhar 1 dia, não 2 seguidos.\n“Você lembra mais dos fracassos ou do progresso que tem feito?”\nComeçar hoje um registro de 3 vitórias diárias e agendar 30min no domingo para revisar a semana.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_46",
                    journeyId = "journey_5",
                    order = 46,
                    title = "Hábito Angular: Uma Mudança que Arrasta o Resto",
                    objective = "selecionar o hábito com maior efeito cascata.",
                    scriptBase = "Hábito angular: um hábito que puxa vários outros (ex.: exercício diário, planejamento matinal, sono).\nFocar em 1 hábito por 66–90 dias em vez de tentar mudar tudo de uma vez.\n“Qual hábito, se instalado, facilitaria ou tornaria outros 5 hábitos quase inevitáveis?”\nListar 5 áreas para melhorar, escolher 1 hábito angular e comprometer‑se com 90 dias de foco nesse hábito.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_47",
                    journeyId = "journey_5",
                    order = 47,
                    title = "Loop de Hábito: Deixa, Rotina e Recompensa",
                    objective = "entender por que hábitos ruins persistem e como substituí‑los.",
                    scriptBase = "Deixa → rotina → recompensa; mudar a rotina mantendo deixa e recompensa desejada.\nEx.: estresse → scroll infinito → alívio → trocar por caminhada curta, respiração ou journaling.\n“Qual hábito ruim mais te sabota hoje e qual recompensa ele te dá?”\nMapear o loop de 1 hábito ruim e escrever uma rotina substituta que dê recompensa parecida.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_48",
                    journeyId = "journey_5",
                    order = 48,
                    title = "Contexto Social, Falhas e Força de Vontade Limitada",
                    objective = "ajustar contexto para não depender só de força de vontade.",
                    scriptBase = "Erros comuns: tentar mudar tudo de uma vez, ignorar contexto social, achar que força de vontade é infinita, desistir antes da automatização.\nSoluções: comunidades alinhadas, accountability externo, planejar falhas, automatizar decisões triviais.\n“Quem ao seu redor reforça seus novos hábitos e quem puxa você de volta pro padrão antigo?”\nDefinir 1 pessoa para ser “parceiro de accountability” e 1 mudança no ambiente físico que facilite o hábito angular.",
                    estimatedDurationMin = 15
                ),
                Chapter(
                    id = "chapter_49",
                    journeyId = "journey_5",
                    order = 49,
                    title = "Plano de Ação em 3 Níveis (Hoje, Semana, 90 Dias)",
                    objective = "transformar tudo em ações graduais, com contingência.",
                    scriptBase = "Nível 1 (Hoje): identificar hábito angular, mapear loop de 1 hábito ruim, criar plano “se-então”, preparar ambiente, fazer compromisso público.\nNível 2 (Semana): executar hábito angular diariamente, registrar sensação, ajustar; começar a testar loops substitutos.\nNível 3 (90 dias): meta mínima de 90 dias de consistência, plano para lidar com falhas sem drama.\n“Qual será o sinal claro de que você se tornou a pessoa que esses hábitos descrevem?”\nEscolher 1 ação de hoje, 1 desta semana e 1 para os próximos 90 dias e registrá‑las dentro do app como “compromissos titãs”.",
                    estimatedDurationMin = 15
                ),
            )
        ),
    )
}
