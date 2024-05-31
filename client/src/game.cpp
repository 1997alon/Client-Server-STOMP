#include "../include/game.h"
#include "../include/event.h"

Game::Game(std::string teama, std::string teamb) : team_a_name(teama), team_b_name(teamb), active(true), time(-1), before_halftime(true), team_a_goals(0), 
team_a_possession(50), team_b_goals(0), team_b_possession(50), details() {
}

Game::~Game(){}

std::string& Game::aName(){ return team_a_name;}


std::string& Game::bName(){ return team_b_name;}

void Game::setActive(bool b){active = b;}

void Game::setBeforeHalf(bool b){before_halftime = b;}

bool Game::isActive(){return active;}

bool Game::beforeHalf(){return before_halftime;}

void Game::set_a_goals(int g){team_a_goals=g;}

void Game::set_b_goals(int g){team_b_goals=g;}

void Game::set_a_possession(int p){team_a_possession = p;}

void Game::set_b_possession(int p){team_b_possession=p;}

int Game::a_goals(){return team_a_goals;}

int Game::a_possession(){return team_a_possession;}

int Game::b_goals(){return team_b_goals;}

int Game::b_possession(){return team_b_possession;}

int Game::getTime(){return time;}

void Game::setTime(int t){time = t;}

void Game::addDetail(std::string detail){
    //int i = details.size();
    details.push_back(detail);
}

std::vector<std::string>& Game::getDetails(){return details;}