-- Updated Signatures
sig Username{}
sig Password{}
sig GitHubName{}
sig GitHubRepoLink{
    identifier: one Int --Identifier useful for have different repoLinks
}{identifier >= 0}
sig DateTime{
    value: Int --Useful for simulate date comparisons
}{value >= 0}

sig CodeTest{}
sig ABS{}
sig Macrovariables{}
sig Boolean{
    value: one Int
}{value = 0 or value = 1}
sig Score{
    var value: one Int
}{value >= 0}

--Using ConsolidationStage as a boolean with start and end of the stage
sig ConsolidationStage {
    active: one Boolean,
    start: one DateTime,
    end: one DateTime
}

-- User related signatures
abstract sig User{
    username: one Username,
    password: one Password,
}

sig Educator extends User{
    tournamentsCreated: set Tournament,
    battlesCreated: set Battle
}

sig Student extends User{
    githubName: one GitHubName,
    tournaments: set Tournament,
    team: some Team, -- can change team on different tournaments
}

-- Tournament-related signatures
sig Team{
    members: some Student,
    participation: one Battle,
    tournament: one Tournament,
    score: one Score
}

sig Tournament{
    regDeadline: one DateTime,
    end: one DateTime,
    battles: some Battle,
    badges: set Badge,
    closed: lone DateTime, --it exploits the difference between the end Date and the real closing Date
    ranks: some Score
}

-- Battle-related signatures
sig Battle{
    minTeamSize: one Int,
    maxTeamSize: one Int,
    regDeadline: one DateTime,
    subDeadline: one DateTime,
    manualEvaluation: one Boolean,
    repositoryLink: one GitHubRepoLink, 
    educator: one Educator,
    code: one Code,
    additionalConfig: one Boolean,
    consolidationStage: one ConsolidationStage,
    teams: set Team,
    ranks: some Score
}

sig Code{
    tests: one CodeTest,
    automationBuildScripts: set ABS,
    sourceCode: one String,
    battle: one Battle
}

-- Badge-related signatures
sig Badge{
    tournament: one Tournament,
    macrovariables: some Macrovariables,
    students: some Student
}

-- Notifications
abstract sig Notification {
    text: String
}

one sig TournamentNotification extends Notification {
    tournament: one Tournament
}

one sig BattleNotification extends Notification {
    battle: one Battle
}

-- PREDICATES

-- Predicate for checking if a user has joined a battle
pred hasJoinedBattle[user: User, t: Tournament, b: Battle] {
    some team: Team | team.members = user && team.participation = b && team.tournament = t
}

pred isBadgeEligible[s: Student, b: Badge, t: Tournament] {
    b in t.badges
    some mc: Macrovariables | mc in b.macrovariables
}

pred requiresManualEvaluation[b: Battle] {
    b.manualEvaluation.value = 1
}


-- FACTS AND CONSTRAINTS

-- Fact: Educators can only create battles in their own tournaments
fact educatorCreatesBattleInOwnTournament {
    all e: Educator, t: Tournament, b: Battle | b in e.battlesCreated implies b in t.battles
}

-- Constraint: Submission deadline must be after registration deadline in a battle
fact submissionAfterRegistration {
    all b: Battle | b.subDeadline.value < b.regDeadline.value
}

fact consolidationStageOnlyIfManualEvaluation {
    all b: Battle | (b.consolidationStage.value = 1) implies requiresManualEvaluation[b]
}

-- Fact: Each team in a tournament participates in at least one battle
fact teamsParticipateInBattles {
    all t: Tournament, team: Team | team.tournament = t implies some b: Battle | b in t.battles && team.participation = b
}

-- Fact: A student can only be a member of one team in a tournament
fact studentBelongsToOneTeam {
    all t: Tournament, s: Student, team1, team2: Team |
        team1.members = s && team1.tournament = t && team2.members = s && team2.tournament = t implies team1 = team2
}

-- Fact: The registration deadline of a battle must be before the tournament end
fact registrationBeforeTournamentEnd {
    all t: Tournament, b: Battle | b.tournament = t implies b.regDeadline in t.end
}

-- Fact: A student can only join a tournament if they are not already a member of another team in the same tournament
fact studentJoinsTournamentOnce {
    all t: Tournament, s: Student, team1, team2: Team |
        team1.members = s && team1.tournament = t && team2.members = s && team2.tournament = t implies team1 = team2
}

-- Constraint: Consolidation stage must have a start time before the end time
fact consolidationStageTimeOrder {
    all cs: ConsolidationStage | cs.start.value <= cs.end.value
}

-- Fact: Each badge is associated with exactly one tournament
fact badgeAssociatedWithTournament {
    all b: Badge | b.tournament in Tournament
}

-- Constraint: If a battle requires manual evaluation, it must have a consolidation stage
fact manualEvaluationRequiresConsolidationStage {
    all b: Battle | b.manualEvaluation.value = 1 implies b.consolidationStage in ConsolidationStage
}


-- Constraint: Teams must have a score after a battle ends
fact teamsHaveScoreAfterBattle {
    all b: Battle | all t: b.teams | some s: Score | s in t.score
}

-- Fact: Each tournament's badges are always associated with the tournament
fact badgesAssociatedAlwaysWithTournament {
    all t: Tournament, b: Badge | b in t.badges implies always (b.tournament = t)
}

-- Fact: The new score for a tournament has to be at least equal to the previous one
fact NewTournamentScore {
    all t: Tournament | always t.ranks.value >= t'.ranks.value
}

-- Fact: Every team has a relation with the student in that team
fact TeamStudent {
    all t: Team, s: Student | t in s.team implies s in t.members
}

-- Fact: Every user has a different username
fact DifferentUsername {
    no disj u1, u2: User | some u1.username & u2.username
}

-- Fact: Every user has a different GitHubName
fact DifferentGitHubName {
    no disj s1, s2: Student | some s1.githubName & s2.githubName
}

-- Fact: No username without users
fact NoUsernameWithoutUser {
    some Username implies some User
}

-- Fact: A student can obtain only the badges relates to a tournament he partecipated
fact StudentsBadge {
    no disj s: Student, b: Badge | s in b.students and b.tournament not in s.tournaments
}

-- Fact: Every battle has a different GitHubRepo
fact DifferentGitHubRepo {
    no disj b1, b2: Battle | some b1.repositoryLink & b2.repositoryLink
}

-- Fact: Every GitHubRepository has a different URI
fact DifferentURI {
    no disj g1, g2: GitHubRepoLink | some g1.identifier & g2.identifier
}




-- Run the model with these facts and constraints
run {} for 5 but 5 Int, exactly 4 Username, exactly 3 User, exactly 1 Team, exactly 1 Tournament, exactly 2 Battle, exactly 2 Code, exactly 2 Badge, exactly 5 Macrovariables, 3 GitHubRepoLink, exactly 3 String
