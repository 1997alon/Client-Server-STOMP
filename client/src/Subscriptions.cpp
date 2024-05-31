#include "../include/Subscriptions.h"


Subscriptions::Subscriptions(): pending_games(), active_games(), active_games_bystring(){}
Subscriptions::~Subscriptions(){}

void Subscriptions::addPendSubscription(std::string game, int id){
    pending_games.insert(std::make_pair(id, game));
}

void Subscriptions::activateSubscription(int id){
    std::string game = pending_games.find(id) -> second;
    pending_games.erase(id);
    active_games.insert(std::make_pair(id, game));
    active_games_bystring.insert(std::make_pair(game, id));
}

void Subscriptions::removeSubscription(int id){
    active_games.erase(id);
}

int Subscriptions::getIdbyGame(std::string game){
    return active_games_bystring.find(game) -> second;
}

std::string Subscriptions::getGamebyId(int id){
    return active_games.find(id) -> second;
}
void Subscriptions::clear(){
    active_games.clear();
    active_games_bystring.clear();
    pending_games.clear();
}
bool Subscriptions::contains(std::string st){
    if (active_games_bystring.count(st) > 0)
        return true;
    return false;
}