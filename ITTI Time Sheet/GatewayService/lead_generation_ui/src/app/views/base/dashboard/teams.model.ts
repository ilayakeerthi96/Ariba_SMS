export interface TeamMember {
  employeeCode: string;
  name: string;
  // ⭐ FIX 1: Add 'Management' to the allowed types here
  designation: 'Sales Manager' | 'Sales Person' | 'Management'; 
}

export interface Team {
  // ⭐ FIX 2: Add 'Management' to the allowed team names here
  teamName: 'Business Development' | 'Professional Services' | 'Management';
  manager: string | string[];
  members: TeamMember[];
}

export const TEAMS: Team[] = [
  // ⭐ FIX 3: Add the Management Team block for Harsha
  {
    teamName: 'Management',
    manager: ['2267',"20303"],
    members: [
      { employeeCode: "2267", name: 'Harsha', designation: 'Management' },
      { employeeCode: "20303", name: 'Vaibhaw Kumar', designation: 'Management' }
    ]
  }, 
  {
    teamName: 'Business Development',
    manager: '9071',
    members: [
      { employeeCode: "9071", name: 'Firoz', designation: 'Sales Manager' },
      { employeeCode: "2178", name: 'Humayun Rashid', designation: 'Sales Person' }
    ]
  },
  
  {
    teamName: 'Business Development',
    manager: ['2567',"9071"], 
    members: [
      { employeeCode: "2567", name: 'S Swathi', designation: 'Sales Manager' },
      { employeeCode: "2888", name: 'Drakshyani', designation: 'Sales Person' }
    ]
  },

  {
    teamName: 'Professional Services',
    manager: '2578',
    members: [
      { employeeCode: "2578", name: 'Rinku Ray', designation: 'Sales Manager' }
    ]
  },

  {
    teamName: 'Professional Services',
    manager: '9051',
    members: [
      { employeeCode: "9051", name: 'Rajat', designation: 'Sales Manager' }
    ]
  },
    
  {
    teamName: 'Professional Services',
    manager: '6218',
    members: [
      { employeeCode: "6218", name: 'Dinesh', designation: 'Sales Manager' },
      { employeeCode: "2936", name: 'Ramesh', designation: 'Sales Person' }
    ]
  }
];