import React from "react";
import "./userAdd.sass";
import moment from "moment";
import SelectLevels from "./selectLevels.js";
import { USER_NORMAL } from "../userStates.js";

class UserAddSection extends React.Component {
    state = {
        username: "",
        level: 1,
    };

    onChangeUsername = (e) => {
        this.setState({
            username: e.target.value,
        });
    };

    onChangeLevel = (e) => {
        this.setState({
            level: parseInt(e.target.value, 10),
        });
    };

    onClickCreateUser = () => {
        if (!this.state.username) {
            return;
        }

        let user = {
            id: this.props.userIdIncr,
            username: this.state.username,
            level: this.state.level,
            isOnline: true,
            queueState: USER_NORMAL,
            queueStateUpdatedAt: moment().toJSON()
        };
        this.props.onAddUser(user);
    };

    render = () => {
        return (
            <article id="add-user">
                <h2>Add User</h2>
                <section className="add-user-form">
                    <div className="username">
                        <h4>Username</h4>
                        <input
                            text="text"
                            value={this.state.username}
                            onChange={this.onChangeUsername}
                        />
                    </div>
                    <div className="level">
                        <h4>Starting Level</h4>
                        <SelectLevels
                            value={this.state.level}
                            onSelect={this.onChangeLevel}
                        />
                    </div>
                    <button onClick={this.onClickCreateUser}>
                        Create User
                    </button>
                </section>
            </article>
        );
    };
}

export default UserAddSection;
