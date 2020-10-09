import React from "react";
import "./App.sass";
import { findIndex, remove } from "lodash";
import moment from "moment";
import UserAddSection from "./components/userAdd.js";
import UserList from "./components/userList.js";
import MatchList from "./components/matchList.js";
import { USER_NORMAL, USER_QUEUED, USER_MATCHED } from "./userStates.js";

class App extends React.Component {
    state = {
        userList: [],
        matchList: [],
        userIdIncr: 2,
        matchIdIncr: 3,
    };

    componentDidMount() {
        this.setState({
            userList: [
                {
                    id: 1,
                    username: "ahsu",
                    level: 5,
                    isOnline: true,
                    state: USER_NORMAL,
                },
                {
                    id: 10,
                    username: "BSJ",
                    level: 8,
                    isOnline: true,
                    state: USER_MATCHED,
                    stateChangedAt: moment(),
                }
            ],
            matchList: [
                {
                    id: 1,
                    startedAt: moment(),
                    users: [
                        { id: 4, username: "Ana", level: 4 },
                        { id: 5, username: "Jerax", level: 4 },
                        { id: 6, username: "Topson", level: 4 },
                    ],
                },
                {
                    id: 2,
                    startedAt: moment(),
                    users: [
                        { id: 7, username: "Kuroky", level: 7 },
                        { id: 8, username: "Miracle", level: 6 },
                        { id: 9, username: "MindControl", level: 6 },
                    ],
                }
            ]
        });
    }

    onAddUser = (user) => {
        this.state.userList.push(user);
        this.setState({
            userList: this.state.userList,
            userIdIncr: this.state.userIdIncr + 1,
        });
    };

    onUpdateUser = (user) => {
        let foundUserIndex = findIndex(this.state.userList, { id: user.id });
        this.state.userList[foundUserIndex] = user;
        this.setState({
            userList: this.state.userList,
        });
    };

    onCreateMatch = (match) => {
        // TODO: This should be automatic from webserver
        // When enough users are queued on backend, 
        // we need to be alerted that a match has been created!
        const users = match.users;

        // change state of all users in match
        users.forEach((user) => {
            user.state = USER_MATCHED;
        });

        // add match to list of matches
        this.state.matchList.push(match);
        this.setState({
            matchList: this.state.matchList,
            matchIdIncr: this.state.matchIdIncr + 1,
        });
    };

    onEndMatch = (match) => {
        let matchList = this.state.matchList;
        remove(matchList, { id: match.id });
        this.setState({
            matchList: matchList,
        });
    };

    render = () => {
        return (
            <div className="App">
                <header className="App-header">
                    <h1>Matchmaker Demo</h1>
                </header>
                <main className="App-main">
                    <UserAddSection
                        onAddUser={this.onAddUser}
                        userIdIncr={this.state.userIdIncr}
                    />
                    <UserList
                        users={this.state.userList}
                        onUpdateUser={this.onUpdateUser}
                    />
                    <MatchList
                        matches={this.state.matchList}
                        onEndMatch={this.onEndMatch}
                    />
                </main>
            </div>
        );
    };
}

export default App;
