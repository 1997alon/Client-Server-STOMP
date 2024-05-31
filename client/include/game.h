#pragma once

#include <string>
#include <iostream>
#include <map>
#include "event.h"
#include <vector>

class Game
{
private:
    std::string team_a_name;
    std::string team_b_name;
    bool active;
    int time;
    bool before_halftime;
    int team_a_goals;
    int team_a_possession;
    int team_b_goals;
    int team_b_possession;
    std::vector<std::string> details;

public:
    Game(std::string teama, std::string teamb);
    virtual ~Game();
    std::string& aName();
    std::string& bName();
    void setActive(bool b);
    void setBeforeHalf(bool b);
    bool isActive();
    bool beforeHalf();
    void set_a_goals(int g);
    void set_b_goals(int g);
    void set_a_possession(int p);
    void set_b_possession(int p);
    int a_goals();
    int a_possession();
    int b_goals();
    int b_possession();
    int getTime();
    void setTime(int t);
    void addDetail(std::string detail);
    std::vector<std::string>& getDetails();
};

