var _createClass = function () {
    function defineProperties(target, props) {
        for (var i = 0; i < props.length; i++) {
            var descriptor = props[i];
            descriptor.enumerable = descriptor.enumerable || false;
            descriptor.configurable = true;
            if ("value" in descriptor) {
                descriptor.writable = true;
            }
            Object.defineProperty(target, descriptor.key, descriptor);
        }
    }

    return function (Constructor, protoProps, staticProps) {
        if (protoProps) {
            defineProperties(Constructor.prototype, protoProps);
        }
        if (staticProps) {
            defineProperties(Constructor, staticProps);
        }
        return Constructor;
    };
}();

function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
        throw new TypeError("Cannot call a class as a function");
    }
}

function _possibleConstructorReturn(self, call) {
    if (!self) {
        throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
    }
    return call && (typeof call === "object" || typeof call === "function") ? call : self;
}

function _inherits(subClass, superClass) {
    if (typeof superClass !== "function" && superClass !== null) {
        throw new TypeError("Super expression must either be null or a function, not " + typeof superClass);
    }
    subClass.prototype = Object.create(superClass && superClass.prototype, {
        constructor: {
            value: subClass,
            enumerable: false,
            writable: true,
            configurable: true
        }
    });
    if (superClass) {
        Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass;
    }
}

console.clear();

var Title = function Title(_ref) {
    var todoCount = _ref.todoCount;

    return React.createElement(
        "div", null,
        React.createElement(
            "div", null,
            React.createElement( "h1", null, "to-do (", todoCount, ")" )
        )
    );
};

var TodoForm = function TodoForm(_ref2) {
    var addTodo = _ref2.addTodo;

    // Input Tracker
    var input = void 0;
    // Return JSX
    return React.createElement(
        "form", {
            onSubmit: function onSubmit(e) {
                e.preventDefault();
                addTodo(input.value);
                input.value = '';
            }
        },
        React.createElement("input", {
            className: "form-control col-md-12", ref: function ref(node) {
                input = node;
            }
        }),
        React.createElement("br", null)
    );
};

var Todo = function Todo(_ref3) {
    var todo = _ref3.todo;
    var remove = _ref3.remove;

    // Each Todo
    return React.createElement(
        "a", {
            href: "#", className: "list-group-item", onClick: function onClick() {
                remove(todo.id);
            }
        }, todo.todoText
    );
};

var TodoList = function TodoList(_ref4) {
    var todos = _ref4.todos,
        remove = _ref4.remove;

    // Map through the todos
    var todoNode = todos.map(function (todo) {
        return React.createElement(Todo, {todo: todo, key: todo.id, remove: remove});
    });
    return React.createElement("div", { className: "list-group", style: {marginTop: '30px'} }, todoNode);
};

// Contaner Component
// Todo Id
window.id = 0;

var TodoApp = function (_React$Component) {
    _inherits(TodoApp, _React$Component);

    function TodoApp(props) {
        _classCallCheck(this, TodoApp);

        // Set initial state
        var _this = _possibleConstructorReturn(this, (TodoApp.__proto__ || Object.getPrototypeOf(TodoApp)).call(this, props));
        // Pass props to parent class


        _this.state = {
            data: []
        };
        _this.apiUrl = '/api/todo';
        return _this;
    }

    // Lifecycle method
    _createClass(TodoApp, [{
        key: "componentDidMount",
        value: function componentDidMount() {
            var _this2 = this;

            // Make HTTP reques with Axios
            axios.get(this.apiUrl).then(function (res) {
                // Set state with result
                _this2.setState({data: res.data});
            });
        }
    }, {
        key: "addTodo",
        value: function addTodo(val) {
            var _this3 = this;

            // Assemble data
            var todo = {
                todoText: val
            };
            axios.post(this.apiUrl, todo).then(function (res) {
                _this3.state.data.push(res.data);
                _this3.setState({data: _this3.state.data});
            });
        }
    }, {
        // Handle remove
        key: "handleRemove",
        value: function handleRemove(id) {
            var _this4 = this;

            // Filter all todos except the one to be removed
            var remainder = this.state.data.filter(function (todo) {
                if (todo.id !== id) return todo;
            });
            // Update state with filter
            axios.delete(this.apiUrl + '/' + id).then(function (res) {
                _this4.setState({data: remainder});
            });
        }
    }, {
        key: "render",
        value: function render() {
            // Render JSX
            return React.createElement(
                "div",
                null,
                React.createElement(Title, { todoCount: this.state.data.length}),
                React.createElement(TodoForm, { addTodo: this.addTodo.bind(this)}),
                React.createElement(TodoList, {
                    todos: this.state.data,
                    remove: this.handleRemove.bind(this)
                })
            );
        }
    }]);

    return TodoApp;
}(React.Component);

ReactDOM.render(React.createElement(TodoApp, null), document.getElementById('container'));