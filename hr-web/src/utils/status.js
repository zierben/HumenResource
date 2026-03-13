export const PersonnelStatus = {
  PENDING_ENTRY: 'PENDING_ENTRY',
  ON_DUTY: 'ON_DUTY',
  LEAVE: 'LEAVE',
  TRANSFER: 'TRANSFER',
  OFF_DUTY: 'OFF_DUTY',
  
  getType: (status) => {
    const map = {
      'PENDING_ENTRY': 'info',
      'ON_DUTY': 'success',
      'LEAVE': 'warning',
      'TRANSFER': 'primary',
      'OFF_DUTY': 'danger'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      'PENDING_ENTRY': '待入场',
      'ON_DUTY': '在岗',
      'LEAVE': '请假',
      'TRANSFER': '调配中',
      'OFF_DUTY': '已离场'
    }
    return map[status] || '未知'
  }
}

export const RequirementStatus = {
  DRAFT: 0,
  PENDING_DEPT: 1,
  PENDING_HR: 2,
  PENDING_FINANCE: 3,
  APPROVED: 4,
  REJECTED: 5,
  
  getType: (status) => {
    const map = {
      0: 'info',
      1: 'warning',
      2: 'warning',
      3: 'warning',
      4: 'success',
      5: 'danger'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      0: '草稿',
      1: '待部门审批',
      2: '待HR审批',
      3: '待财务审批',
      4: '已通过',
      5: '已拒绝'
    }
    return map[status] || '未知'
  }
}

export const SettlementStatus = {
  PENDING: 0,
  HR_CONFIRMED: 1,
  PM_CONFIRMED: 2,
  FINANCE_CONFIRMED: 3,
  SUPPLIER_CONFIRMED: 4,
  PAID: 5,
  
  getType: (status) => {
    const map = {
      0: 'warning',
      1: 'info',
      2: 'info',
      3: 'info',
      4: 'primary',
      5: 'success'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      0: '待确认',
      1: 'HR已确认',
      2: 'PM已确认',
      3: '财务已确认',
      4: '供应商已确认',
      5: '已支付'
    }
    return map[status] || '未知'
  }
}

export const WorkHoursStatus = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
  
  getType: (status) => {
    const map = {
      'PENDING': 'warning',
      'APPROVED': 'success',
      'REJECTED': 'danger'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      'PENDING': '待审批',
      'APPROVED': '已通过',
      'REJECTED': '已拒绝'
    }
    return map[status] || '未知'
  }
}

export const SupplierLevel = {
  A: 'A',
  B: 'B',
  C: 'C',
  
  getType: (level) => {
    const map = {
      'A': 'success',
      'B': 'warning',
      'C': 'danger'
    }
    return map[level] || 'info'
  },
  
  getText: (level) => {
    const map = {
      'A': 'A级(优秀)',
      'B': 'B级(良好)',
      'C': 'C级(一般)'
    }
    return map[level] || '未知'
  }
}

export const ProjectStatus = {
  PLANNING: 'PLANNING',
  ACTIVE: 'ACTIVE',
  COMPLETED: 'COMPLETED',
  SUSPENDED: 'SUSPENDED',
  
  getType: (status) => {
    const map = {
      'PLANNING': 'info',
      'ACTIVE': 'success',
      'COMPLETED': 'primary',
      'SUSPENDED': 'danger'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      'PLANNING': '规划中',
      'ACTIVE': '进行中',
      'COMPLETED': '已完成',
      'SUSPENDED': '已暂停'
    }
    return map[status] || '未知'
  }
}

export const CandidateStatus = {
  PENDING: 'PENDING',
  INTERVIEWING: 'INTERVIEWING',
  OFFERED: 'OFFERED',
  HIRED: 'HIRED',
  REJECTED: 'REJECTED',
  
  getType: (status) => {
    const map = {
      'PENDING': 'info',
      'INTERVIEWING': 'warning',
      'OFFERED': 'primary',
      'HIRED': 'success',
      'REJECTED': 'danger'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      'PENDING': '待筛选',
      'INTERVIEWING': '面试中',
      'OFFERED': '已发Offer',
      'HIRED': '已入职',
      'REJECTED': '已拒绝'
    }
    return map[status] || '未知'
  }
}

export const ContractStatus = {
  ACTIVE: 'ACTIVE',
  EXPIRING: 'EXPIRING',
  EXPIRED: 'EXPIRED',
  
  getType: (status) => {
    const map = {
      'ACTIVE': 'success',
      'EXPIRING': 'warning',
      'EXPIRED': 'danger'
    }
    return map[status] || 'info'
  },
  
  getText: (status) => {
    const map = {
      'ACTIVE': '生效中',
      'EXPIRING': '即将到期',
      'EXPIRED': '已过期'
    }
    return map[status] || '未知'
  }
}
