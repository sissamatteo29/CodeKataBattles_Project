-- SIGNATURES (lexicographically orderded)

sig ABS {} --Automation Build Scripts

sig Badge {
    macrovariables: some Macrovariables,
    students: some Student,
    tournament: one Tournament
}

sig Battle {
    material: one EducatorMaterial,
    teams: set Team,
    educator: one Educator,
    tournament: one Tournament,
    maxTeamSize: one Int,
    minTeamSize: one Int,
    ranking: set Score,
    repositoryLink: one GitHubRepoLink,
    consolidationStage: one ConsolidationStage,
    manualEvaluation: one Boolean,
    regDeadline: one DateTime,
    subDeadline: one DateTime,
    additionalConfig: one EvaluationCriteria,
}{minTeamSize > 0 and minTeamSize < maxTeamSize and subDeadline.value > 0}

sig BattleNotification extends Notification {
    battle: one Battle
}

sig Boolean {
    value: one Int
}{value = 0 or value = 1}

sig CodeTest {}

sig ConsolidationStage {
    start: one DateTime, --useful to model the world, not used in practice
    end: one DateTime --as the above
    -- a start = 0 and an end = 0 represent that there is no ConsolidationState
}

sig DateTime {
    value: Int
}{value >= 0} --in order to simplify the signature, dates will be compared only by one value

sig Description {}

sig Educator extends User {
    battlesCreated: set Battle,
    tournamentsCreated: set Tournament,
    tournamentWithPermission: set Tournament --they include the tournaments he create
}

sig EducatorMaterial {
    automationBuildScripts: set ABS,
    textualDescription: one Description,
    tests: one CodeTest 
} --it is unlikely but possible that two different battles have the same EducatorMaterial

sig EvaluationCriteria {} --to be specified in the implementation

abstract sig Notification {}

sig GitHubName {}

sig GitHubRepoLink {
    identifier: one Int
}{identifier >= 0}

sig Macrovariables {} --to be specified in the implementation

sig GitHubPassword {}

sig Score {} --to be specified in the implementation

sig Student extends User {
    team: set Team, --the partecipation to a tournament is reached through team
    tournaments: set Tournament --students without tournaments can exists
}

sig Team {
    members: some Student,
    participationToBattle: one Battle,
    score: one Score,
}{#members >= 1} --the student itself is treated as a one member team

sig Tournament {
    creator: one Educator,
    badges: set Badge,
    var battles: some Battle,
    ended: one Boolean, --if 1 the Date of end is different from 0 
    endDate: one DateTime, --the date when the tournament is ended, 0 if it is not ended
    ranking: some Score
}{ended.value > 0}

sig TournamentNotification extends Notification {
    tournament: one Tournament
}

sig User {
    password: one GitHubPassword,
    username: one GitHubName
}

-------------------------------------------------------------

-- FACTS (lexicographically ordered)

-- General considerations: in our world GitHubNames, GitHubRepoLinks and EducatorMaterial can exist, but it cannot exist a user without a proper GitHubName or a Battle without a proper GitHubRepoLink and a proper EducatorMaterial

-- Fact: Badge and tournament are biunivocally related
fact badgesAlwaysAssociatedWithTournament {
    all t: Tournament, b: Badge | b in t.badges implies b.tournament = t
    all t: Tournament, b: Badge | t = b.tournament implies b in t.badges
}

-- Fact: Battles and tournament are biunivocally related
fact battleAlwaysAssociatedWithTournament {
    all t: Tournament, b: Battle | t in b.tournament implies b.tournament = t
    all t: Tournament, b: Battle | b.tournament = t implies b in t.battles 
}

-- Fact: Every battle created by and educator belongs to the set of battles created by that educator
fact battleCreatedForEducators {
    all e: Educator, b: Battle | b.educator = e implies b in e.battlesCreated
}

-- Fact: Two educators can't create the same battle
fact battleCreatedByOneEducator {
    no disj e1, e2: Educator, b: Battle | b in e1.battlesCreated and b in e2.battlesCreated 
}

-- Fact: A battle must end before the endDate of a tournament
fact battleEndInTournament {
    all b: Battle, t: Tournament | b in t.battles implies b.subDeadline.value < t.endDate.value 
}

-- Fact: We need to define the correct time order of the consolidation stage, when its value is different from the error state (when both the start and specifically the end values equal zero)
fact consolidationStageTimeOrder {
    all cs: ConsolidationStage | cs.end.value!=0 implies cs.start.value < cs.end.value --it is enough to consider only the end value: in no case it could be zero
}

-- Fact: DifferentUsername, so different GitHubNames
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

-- Fact: Educator creates a battle in own tournament or in a tournament he has a permission for
fact educatorCreatesBattleInOwnTournament {
    all e: Educator, t: Tournament, b: Battle | b in e.battlesCreated and (t in e.tournamentWithPermission or t in e.tournamentsCreated) implies b in t.battles
}

-- Fact: Every team partecipating to a battle has a score
fact everyTeamHasItsScore {
    all t: Tournament, b1, b2: Battle | b1 in t.battles and b2 in t.battles and #b1.ranking != 0 and #b2.ranking != 0 implies #t.ranking = #b1.ranking + #b2.ranking
}

-- Fact: Every tournament that is created by an educator is in his tournamentWithPermission
fact everyTournamentCreatedGivesPermissions {
    all t: Tournament, e: Educator | t in e.tournamentsCreated implies t in e.tournamentWithPermission
}

-- Fact: Manual evaluation requires the consolidation stage
fact manualEvaluationRequiresConsolidationStage {
    all b: Battle | (b.manualEvaluation.value = 0 implies b.consolidationStage.start.value = 0 and b.consolidationStage.end.value = 0) and 
                (b.manualEvaluation.value = 1 implies b.consolidationStage.start.value != 0 and b.consolidationStage.end.value != 0)
}

-- Fact: Every user must be a student or an educator
fact noUserWithoutRole {
    Student + Educator = User
}

-- Fact: If a battle has no teams involved, it produces no scores
fact noScoreWithoutATeam {
    all b: Battle | #b.teams = 0 implies #b.ranking = 0 
    all b: Battle | #b.teams != 0 implies #b.ranking != 0
}

-- Fact: The registration must be done before the ending of the tournament
fact registrationBeforeTournamentEnd {
    all t: Tournament, b: Battle | b.tournament = t implies b.regDeadline.value < t.endDate.value
}

-- Fact: Students can't join a tournament more than one time
fact studentJoinsTournamentOnce {
    all t: Tournament, s: Student, team1, team2: Team |
        team1.members = s && team1.members.tournaments = t && team2.members = s && team2.members.tournaments = t implies team1 = team2
}

-- Fact: If a student is a member of a team partecipating to a battle, the tournament to which that battle belongs belongs to the tournament to which that student is subscribed
fact studentParticipatesToTournament {
    all s: Student, team: Team, b: Battle | s in team.members and b in team.participationToBattle implies b.tournament in s.tournaments
}

-- Fact: A student can receive a badge only from a tournament he partecipated to
fact studentsBadge {
    no disj s: Student, b: Badge | s in b.students and b.tournament not in s.team.participationToBattle.tournament
}

-- Fact: A student can have a tournament belonging in his tournaments only if it partecipated to that tournament
fact studentsTournamentIfInTeam {
    all s: Student, t: Tournament | t in s.tournaments implies s in t.battles.teams.members
}

-- Fact: The registration deadline of a battle must be always before the submission deadline
fact submissionAfterRegistration {
    all b: Battle | b.regDeadline.value < b.subDeadline.value
}

-- Fact: After a battle all teams receive a score
fact teamsHaveScoreAfterBattle {
    all b: Battle | all t: b.teams | some s: Score | s in t.score
}

-- Fact: Students and teams are biunivocally related
fact teamStudent {
    all t: Team, s: Student | t in s.team implies s in t.members
    all t: Team, s: Student | s in t.members implies t in s.team
}

-- Fact: All teams partecipating to a battle are teams of that battle
fact teamBattle {
    all t: Team, b: Battle | t in b.teams implies b in t.participationToBattle
}

-- Fact: Every tournament can be created only by one educator
fact tournamentsCreatedByOneEducator {
    no disj e1, e2: Educator, t: Tournament | t in e1.tournamentsCreated and t in e2.tournamentsCreated
}

-- Fact: All creator's tournaments are tournaments created by him
fact tournamentCreatedForEducators {
    all t: Tournament, e: Educator | t.creator = e implies t in e.tournamentsCreated
}

-- Fact: A tournament has a date of ending only if its boolean value "ended" equals one
fact tournamentEndedIfBoolean {
    all t: Tournament | t.ended.value = 1 implies t.endDate.value != 0
}

-- Fact: There are no tournaments with the same battle
fact tournamentHaveDifferentBattles {
    no disj t1, t2: Tournament, b: Battle | b in t1.battles and b in t2.battles
}

-- Fact: If an educator creates battles in a tournament, he has the permission for it (different from creating a tournament)
fact tournamentsWithPermissionsForEducatorsCreatingBattlesInThem {
    all e: Educator, b: Battle | b in e.battlesCreated implies b.tournament in e.tournamentWithPermission 
}

-- Fact: Every educator material is different
fact uniqueSourceCodeTestABS {
    all c1, c2: EducatorMaterial | c1 != c2 implies {
        c1.textualDescription != c2.textualDescription &&
        c1.tests != c2.tests &&
        c1.automationBuildScripts != c2.automationBuildScripts
    }
}

-------------------------------------------------------------

-- PREDICATES useful for asserts
pred addBattleToATournament[t: Tournament, b: Battle] {
    t.battles' = t.battles + b
}

pred delBattleFromATournament[t: Tournament, b: Battle] {
    t.battles' = t.battles - b
}

pred addTournament[e: Educator, t: Tournament] {
    e.tournamentsCreated' = (e.tournamentsCreated) + t
}

pred studentLeavesATeam[s: Student, t: Team] {
    s.team' = s.team - t
}
-- The reader should be careful that these predicates are correctly generating expected worlds only when used in the following assertion: 
-- In fact, it is up to the assertion to avoid the situations where an union wants to add an object already part of the set or where a subtraction wants to delete an object not belonging to the set

-------------------------------------------------------------

-- ASSERTIONS
-- Assertion: when adding a battle not previously belonging to a tournament, the total number of the battles of the tournament is increased by one
assert addBattle {
   all t: Tournament, b: Battle | before b not in t.battles and after addBattleToATournament[t, b] implies always #t.battles' = (#t.battles + 1)
}
--check addBattle for 5

-- Not considering the case of adding a battle not previously belonging to a tournament where the educator who created that tournament will have this battle belonging to his created battles, because there could be a lot of educators with permissions, not granting he is the one who created the battle added

-- Assertion: when deleting a battle previously belonging to a tournament, the total number of the battles of the toruanement is decreased by one
assert deleteBattle {
   all t: Tournament, b: Battle | before b in t.battles and after delBattleFromATournament[t, b] implies always #t.battles' = (#t.battles - 1)
}
--check deleteBattle for 5

-- Assertion: when adding a tournament by an educator not previously belonging to the tournaments he created, the total number of his created tournament is increased by one
assert addTournament {
   all t: Tournament, e: Educator | before t not in e.tournamentsCreated and after addTournament[e, t] implies always #e.tournamentsCreated' = (#e.tournamentsCreated + 1)
}
--check addTournament for 5

-- Assertion: when deleting a battle previously belonging to a tournament, and then adding it to that tournament, the world represented after two times will be the initial one
assert delBattleAfterInsert {
   all t: Tournament, b: Battle | before b in t.battles and addBattleToATournament[t, b] and after delBattleFromATournament[t, b] implies t.battles'' = t.battles
}
--check delBattleAfterInsert for 5

-- Assertion: when a student leaves his team, then he will be no more a member of that team
assert studentLeavesHisTeam {
   all s: Student, t: Team | before t in s.team and after studentLeavesATeam[s, t] implies t not in s.team'
}
--check studentLeavesHisTeam for 5

-- Assertion: checking that the start and end value of the consolidation stage are different from zero only when the educator asked for the manual evaluation
assert consolidationStageOnlyIfManualEvaluation {
   all b: Battle | b.manualEvaluation.value = 0 implies 
      b.consolidationStage.start.value = 0 and b.consolidationStage.end.value = 0
   all b: Battle | b.manualEvaluation.value = 1 implies 
      b.consolidationStage.start.value != 0 and b.consolidationStage.end.value != 0
}
--check consolidationStageOnlyIfManualEvaluation for 5

-------------------------------------------------------------

-- RUN
-- A general world. The objects represented are a few in other to make the generated image readable
pred general[] {
    #GitHubName = 4
    #Educator = 1
    #Student = 3
    #{t: Team | #t.members > 1} = 1
    #Tournament = 1
    #Battle = 2
    #GitHubRepoLink = 3
}

-- A world where there are two battle, one with the manual evalution, the other without it
pred noManualEvaluation[] {
    #GitHubName = 4
    #Educator = 1
    #Student = 2
    #Team = 1
    #Badge = 2
    #GitHubRepoLink = 3
    #{b1, b2: Battle | b1.manualEvaluation.value = 0 && b2.manualEvaluation.value = 1} = 1
}

-- A world where there are educator creating tournaments, and other only with permission to a tournament created
pred educatorsWithOnlyPermission[] {
    #Team = 1
    #Tournament = 1
    #Battle = 3
    #{e1, e2: Educator | #e1.tournamentWithPermission = 1 && #e1.tournamentsCreated = 0 and #e2.tournamentsCreated = 1} = 1
}

run general for 4
--run noManualEvaluation for 4
--run educatorsWithOnlyPermission for 4
