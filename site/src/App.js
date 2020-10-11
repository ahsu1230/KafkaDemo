import React from "react";
import "./App.sass";
import { findIndex, keyBy, remove } from "lodash";
import moment from "moment";
import API from "./utils/api.js";
import UserAddSection from "./components/userAdd.js";
import UserList from "./components/userList.js";
import MatchList from "./components/matchList.js";
import SyncNotif from "./components/syncing.js";
import { USER_NORMAL, USER_QUEUED, USER_MATCHED } from "./userStates.js";

class App extends React.Component {
    state = {
        userList: [],
        matchList: [],
        userIdIncr: 1,
        matchIdIncr: 1,
        syncing: false,
    };

    componentDidMount() {
        this.fetchData();
        this.interval = setInterval(() => {
            this.setState({ syncing: true });
            setTimeout(() => {
                this.fetchData();
            }, 2000);
        }, 10000);
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    fetchData = () => {
        API.fetchAllUsers()
            .then(response => response.json())
            .then(data => {
                console.log("Success Users: " + JSON.stringify(data));
                this.setState({
                    userList: data,
                });
                API.fetchAllMatches()
                    .then(response => response.json())
                    .then(data => {
                        console.log("Success Matches: " + JSON.stringify(data));
                        this.setState({
                            matchList: data,
                            syncing: false,
                        });
                    })
                    .catch(error => console.log("Error fetching all matches " + error));
            })
            .catch(error => console.log("Error fetching all users " + error));
    }

    onAddUser = (user) => {
        API.fetchUpsertUser(user)
            .then(response => { 
                this.setState({
                    userIdIncr: this.state.userIdIncr + 1,
                });
                this.fetchData() 
            })
            .catch(error => console.log("Error adding user " + error));
    };

    onUpdateUser = (user) => {
        API.fetchUpsertUser(user)
            .then(response => { this.fetchData() })
            .catch(error => console.log("Error updating user " + error));
    };

    onEndMatch = (match) => {
        API.fetchEndMatch(match.id)
            .then(response => { this.fetchData() })
            .catch(error => console.log("Error ending match " + error))
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
                        userMap={createUserMap(this.state.userList)}
                        onEndMatch={this.onEndMatch}
                    />
                </main>
                <SyncNotif syncing={this.state.syncing}/>
            </div>
        );
    };
}

export default App;

function createUserMap(userList) {
    return keyBy(userList, "id");
}