-- Updated Signatures
sig Username{}
sig Password{}
sig GitHubName{}
sig GitHubRepoLink{
    value: one String
}
sig DateTime{}
sig Test{}
sig ABS{}
sig Macrovariables{}
sig Boolean{
    value: one Int
}
sig Score{
    value: one Int
}

-- User related signatures
abstract sig User{
    username: one Username,
    password: one Password,
    team: one Team
}

sig Educator extends User{
    tournamentsCreated: set Tournament,
    battlesCreated: set Battle
}

sig Student extends User{
    githubName: one GitHubName,
    tournaments: set Tournament
}

-- Tournament-related signatures
sig Team{
    students: some Student,
    participation: one Battle,
    tournament: one Tournament,
    score: one Score
}

sig Tournament{
    regDeadline: one DateTime,
    end: one DateTime,
    battles: some Battle,
    badges: set Badge,
    closed: lone DateTime --it exploits the difference between the end Date and the real closing Date
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
    consolidationStage: one Boolean}

-- Code-related signatures
sig Code{
    tests: set Test,
    automationBuildScripts: set ABS,
    battle: one Battle
}

-- Badge-related signatures
sig Badge{
    tournament: one Tournament,
    macrovariables: some Macrovariables
}

-- Predicates

-- Predicate for checking if a user has joined a battle
pred hasJoinedBattle[user: User, t: Tournament, b: Battle] {
    some team: Team | team.students = user && team.participation = b && team.tournament = t
}

-- Predicate for validating a GitHub repository link
pred isValidRepoLink[link: GitHubRepoLink] {
    "https://github.com/" in link.value
}

pred isBadgeEligible[s: Student, b: Badge, t: Tournament] {
    b in t.badges
    some mc: Macrovariables | mc in b.macrovariables
}

-- Facts and Constraints

-- Fact: Educators can only create battles in their own tournaments
fact educatorCreatesBattleInOwnTournament {
    all e: Educator, t: Tournament, b: Battle | b in e.battlesCreated implies b in t.battles
}

-- Constraint: Submission deadline must be after registration deadline in a battle
fact submissionAfterRegistration {
    all b: Battle | b.subDeadline in b'.regDeadline
}

-- Fact: GitHub repository links are valid
fact validRepoLinks {
    all b: Battle | isValidRepoLink[b.repositoryLink]
}

pred requiresManualEvaluation[b: Battle] {
    b.manualEvaluation.value = 1
}

fact ConsolidationStageOnlyIfManualEvaluation {
    all b: Battle | (b.consolidationStage.value = 1) implies requiresManualEvaluation[b]
}



-- Run the model with these facts and constraints
run {} for 5 but 5 Int, exactly 5 User, exactly 5 Team, exactly 5 Tournament, exactly 5 Battle, exactly 5 Code, exactly 5 Badge, exactly 5 Macrovariables
