-- SIGNATURES (lexicographically orderded)

sig ABS {} --Automation Build Scripts

sig Badge {
    macrovariables: some Macrovariables,
    students: some Student,
    tournament: one Tournament
}

sig Battle {
    code: one Code,
    teams: set Team,
    educator: one Educator,
    maxTeamSize: one Int,
    minTeamSize: one Int,
    ranks: some Score,
    repositoryLink: one GitHubRepoLink,
    consolidationStage: one ConsolidationStage,
    manualEvaluation: one Boolean,
    regDeadline: one DateTime,
    subDeadline: one DateTime,
    additionalConfig: one Boolean,
}

sig BattleNotification extends Notification {
    battle: one Battle
}

sig Boolean {
    value: one Int
}{value = 0 or value = 1}

sig Code {
    automationBuildScripts: set ABS,
    sourceCode: one SourceCode,
    tests: one CodeTest
}

sig CodeTest {}

sig ConsolidationStage {
    active: one Boolean,
    start: one DateTime, --useful to model the world, not used in practice
    end: one DateTime --as the above
}

sig DateTime {
    value: Int
}{value >= 0}

sig Educator extends User {
    battlesCreated: set Battle,
    tournamentsCreated: set Tournament
}

abstract sig Notification {}

sig GitHubName {}

sig GitHubRepoLink {
    identifier: one Int
}{identifier >= 0}

sig Macrovariables {}

sig Password {}

sig Score {
    var value: one Int
}{value >= 0}

sig SourceCode {}

sig Student extends User {
    team: some Team --the partecipation to a tournament is reached through team
}

sig Team {
    members: some Student,
    participationToBattle: one Battle,
    score: one Score,
    participationToTournament: one Tournament
}{#members > 1}

sig Tournament {
    badges: set Badge,
    battles: some Battle,
    end: one DateTime, --the date when the tournament is ended
    ranks: some Score
}

sig TournamentNotification extends Notification {
    tournament: one Tournament
}

sig User {
    password: one Password,
    username: one GitHubName
}


-- PREDICATES used as examples, not useful for facts

-- Predicate for checking if a battle requires Manual Evaluation
pred requiresManualEvaluation[b: Battle] {
    b.manualEvaluation.value = 1
}

-- Predicate for checking if a user has joined a battle
pred hasJoinedBattle[user: User, t: Tournament, b: Battle] {
    some team: Team | team.members = user && team.participationToBattle = b && team.tournament = t
}

-- Predicate for checking if a Badge is Eligible
pred isBadgeEligible[s: Student, b: Badge, t: Tournament] {
    b in t.badges
    some mc: Macrovariables | mc in b.macrovariables
}


-- FACTS (lexicographically ordered)
-- Fact: BadgeAssociatedWithTournament
fact badgeAssociatedWithTournament {
    all b: Badge | b.tournament in Tournament
}

-- Fact: BadgesAssociatedAlwaysWithTournament
fact badgesAssociatedAlwaysWithTournament {
    all t: Tournament, b: Badge | b in t.badges implies always (b.tournament = t)
}

-- Fact: BattleEndInTournament
fact battleEndInTournament {
    all b: Battle, t: Tournament | b in t.battles implies b.subDeadline.value < t.end.value
}

-- Fact: ConsolidationStageTimeOrder
fact consolidationStageTimeOrder {
    all cs: ConsolidationStage | cs.start.value <= cs.end.value
}

-- Fact: DifferentUsername
fact differentUsername {
    no disj u1, u2: User | some u1.username & u2.username
}

-- Fact: Every GitHubRepository has a different URI
fact differentURI {
    no disj g1, g2: GitHubRepoLink | some g1.identifier & g2.identifier
}

-- Fact: Every battle has a different GitHubRepo
fact differentGitHubRepo {
    no disj b1, b2: Battle | some b1.repositoryLink & b2.repositoryLink
}

-- Fact: No username without users
fact noUsernameWithoutUser {
    some GitHubName implies some User
} 

-- Fact: The teams participate in the battles
fact teamsParticipateInBattles {
    all t: Tournament, team: Team | team.tournament = t implies some b: Battle | b in t.battles && team.participationToBattle = b
}

-- Fact: A student can only be a member of one team in a tournament
fact studentBelongsToOneTeam {
    all t: Tournament, s: Student, team1, team2: Team |
        team1.members = s && team1.tournament = t && team2.members = s && team2.tournament = t implies team1 = team2
}

-- Fact: A student can only join a tournament if they are not already a member of another team in the same tournament
fact studentJoinsTournamentOnce {
    all t: Tournament, s: Student, team1, team2: Team |
        team1.members = s && team1.tournament = t && team2.members = s && team2.tournament = t implies team1 = team2
}

-- Fact: RegistrationBeforeTournamentEnd
fact registrationBeforeTournamentEnd {
    all t: Tournament, b: Battle | b.tournament = t implies b.regDeadline in t.end
}

-- Fact: SubmissionAfterRegistration
fact submissionAfterRegistration {
    all b: Battle | b.subDeadline.value < b.regDeadline.value
}

-- Fact: TeamsHaveScoreAfterBattle
fact teamsHaveScoreAfterBattle {
    all b: Battle | all t: b.teams | some s: Score | s in t.score
}

-- Fact: BadgeAssociatedWithTournament
fact badgeAssociatedWithTournament {
    all b: Badge | b.tournament in Tournament
}

-- Fact: ManualEvaluationRequiresConsolidationStage
fact manualEvaluationRequiresConsolidationStage {
    all b: Battle | b.manualEvaluation.value = 1 implies b.consolidationStage.active.value = 1
    all b: Battle | b.manualEvaluation.value = 0 implies b.consolidationStage.active.value = 0
}

-- Fact: NewTournamentScore
fact newTournamentScore {
    all t: Tournament | always t.ranks.value >= t'.ranks.value
}

-- Fact: EducatorCreatesBattleInOwnTournament
fact educatorCreatesBattleInOwnTournament {
    all e: Educator, t: Tournament, b: Battle | b in e.battlesCreated implies b in t.battles
}

-- Fact: StudentsBadge
fact studentsBadge {
    no disj s: Student, b: Badge | s in b.students and b.tournament not in s.team.participationToTournament
}

-- Fact: TeamStudent
fact teamStudent {
    all t: Team, s: Student | t in s.team implies s in t.members
}

-- Fact: UniqueSourceCodeTestABS
fact uniqueSourceCodeTestABS {
    all c1, c2: Code | c1 != c2 implies {
        c1.sourceCode != c2.sourceCode &&
        c1.tests != c2.tests &&
        c1.automationBuildScripts != c2.automationBuildScripts
    }
}



-- RUN
pred general[] {
    #GitHubName = 4
    #User = 3
    #Team = 1
    #Tournament = 1
    #Badge = 2
    #Battle = 2
    #Code = 2
    #Macrovariables = 2
    #GitHubRepoLink = 3
}

pred noManualEvaluation[] {
    #GitHubName = 4
    #User = 3
    #Team = 1
    #Tournament = 1
    #Badge = 2
    #Battle = 2
    #Code = 2
    #Macrovariables = 2
    #GitHubRepoLink = 3
    #{b1, b2: Battle | b1.manualEvaluation.value = 0 && b2.manualEvaluation.value = 1} = 1
}

run noManualEvaluation for 4
